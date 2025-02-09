package io.github.tnas.introspectorfilter.strategy;

import io.github.tnas.introspectorfilter.Node;
import io.github.tnas.introspectorfilter.exception.ExceptionWrapper;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.BitSet;
import java.util.Collection;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class TraversalStrategy {

    Logger logger = LoggerFactory.getLogger(TraversalStrategy.class);

    protected int numThreads;
    protected int heightBound;
    protected int breadthBound;
    protected ExceptionWrapper wrapper;
    protected Set<Class<? extends Annotation>> hierarchicalAnnotations;
    protected Class<? extends Annotation> relationshipsAnnotation;

    protected final Predicate<Field> isFilterableField = f ->
            Stream.of(f.getAnnotations()).anyMatch(a -> a.annotationType().equals(relationshipsAnnotation));

    protected final Predicate<Node> notToProcessNode = n ->
            Objects.isNull(n) || n.height() > this.heightBound || n.breadth() > this.breadthBound;

    protected final BiPredicate<BitSet, AtomicBoolean> hasActiveThreads = (idleThreads, foundValue) ->
            idleThreads.stream().count() < this.numThreads && !foundValue.get();

    protected final BiPredicate<Collection<Node>, AtomicBoolean> hasPendingWork = (nodesList, foundValue) ->
            !nodesList.isEmpty() && !foundValue.get();

    public abstract void traverse(Queue<Node> nodesList, BitSet idleThreads, AtomicBoolean foundValue, String textFilter);

    protected boolean isStringOrWrapper(Object fieldValue) {
        return fieldValue instanceof String || ClassUtils.isPrimitiveWrapper(fieldValue.getClass());
    }

    protected boolean containsTextFilter(String text, String filter) {
        return Objects.nonNull(text) && StringUtils.stripAccents(text.toLowerCase()).contains(filter);
    }

    protected void searchInNodeValue(Node node, String textFilter, AtomicBoolean foundValue) {

        var nodeValue = node.value();

        if (isStringOrWrapper(nodeValue) && containsTextFilter(nodeValue.toString(), textFilter)) {
            foundValue.set(true);
            logger.debug("Searched value found '{}'", textFilter);
        }
    }

    protected boolean isValidParentClass(Class<?> parentClass) {
        return Objects.nonNull(parentClass) &&
                (this.hierarchicalAnnotations.isEmpty()
                        || Stream.of(parentClass.getAnnotations())
                        .map(Annotation::annotationType)
                        .anyMatch(this.hierarchicalAnnotations::contains));
    }

    protected void searchInHierarchy(Collection<Node> nodesList, Node node, AtomicBoolean foundValue, String textFilter) {

        var nodeValue = node.value();
        var nodeValueClass = nodeValue.getClass();
        int heightHop = node.height();

        do { // Hierarchical traversing
            if (Objects.nonNull(this.searchInRelationships(node, nodeValueClass, heightHop, textFilter, nodesList, foundValue))) {
                foundValue.set(true);
                logger.debug("Searched value found '{}'", textFilter);
            }

            nodeValueClass = nodeValueClass.getSuperclass();
            heightHop++;
        } while (isValidParentClass(nodeValueClass) && heightHop <= this.heightBound && !foundValue.get());
    }

    protected Node searchInRelationships(Node node, Class<?> instanceClass, final int height, String textFilter,
                                       Collection<Node> nodesList, AtomicBoolean foundValue) {

        var instance = node.value();

        Predicate<Node> matchTextFilter = n -> {

            var fieldValue = n.value();

            if (isStringOrWrapper(fieldValue)) {
                return containsTextFilter(fieldValue.toString(), textFilter);
            } else if (fieldValue instanceof Collection<?> innerCollection) {
                var iterator = innerCollection.iterator();
                while (iterator.hasNext() && !foundValue.get()) {
                    var element = iterator.next();
                    logger.debug("Add node {} to list", element);
                    nodesList.add(new Node(n.height(), n.breadth() + 1, element));
                }
            } else { // Single class
                nodesList.add(new Node(n.height(), n.breadth() + 1, fieldValue));
            }

            return false;
        };

        return Stream.of(instanceClass.getDeclaredFields())
                .filter(isFilterableField)
                .map(this.wrapper.wrap(f -> new PropertyDescriptor(f.getName(), instance.getClass()).getReadMethod()
                        .invoke(instance)))
                .filter(Objects::nonNull)
                .map(o -> new Node(height, node.breadth(), o))
                .filter(matchTextFilter)
                .findFirst()
                .orElse(null);
    }
}

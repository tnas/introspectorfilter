package io.github.tnas.introspectorfilter.strategy;

import io.github.tnas.introspectorfilter.Node;

import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class FullSharedNodeStrategy extends TraversalStrategy {

    private final Predicate<Node> notToProcessNode = n ->
            Objects.isNull(n) || n.height() > this.heightBound || n.breadth() > this.breadthBound;

    private boolean started;
    private Queue<Node> nodesList;

    @Override
    public void traverse(Object root, AtomicInteger tidCounter, int[] idleThreads, AtomicBoolean foundValue, String textFilter) {

        synchronized (this) {
            if (!started) {
                this.nodesList = new ConcurrentLinkedQueue<>();
                this.nodesList.add(new Node(0, 0, root));
                this.started = true;
            }
        }

        final int tid = tidCounter.getAndIncrement();
        logger.debug("Running Thread {}", tid);

        while (hasActiveThreads(idleThreads, foundValue)) {

            while (hasPendingWork(nodesList, foundValue)) { // BFS for relationships

                var node = nodesList.poll();

                if (notToProcessNode.test(node)) {
                    idleThreads[tid] = IDLE_THREAD;
                    logger.debug("Thread {} idle", tid);
                    continue;
                }

                idleThreads[tid] = ACTIVE_THREAD;

                assert node != null;
                logger.debug("Processing {}", node.value());

                this.searchInHierarchy(nodesList, node, foundValue, textFilter);

                this.searchInNodeValue(node.value(), textFilter, foundValue, tid);

                idleThreads[tid] = IDLE_THREAD;
            }
        }

        this.started = false;
    }

    private void searchInHierarchy(Collection<Node> nodesList, Node node, AtomicBoolean foundValue, String textFilter) {

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

    private Node searchInRelationships(Node node, Class<?> instanceClass, final int height, String textFilter,
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

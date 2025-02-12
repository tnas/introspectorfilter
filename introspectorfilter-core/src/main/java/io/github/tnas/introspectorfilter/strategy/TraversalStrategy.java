package io.github.tnas.introspectorfilter.strategy;

import io.github.tnas.introspectorfilter.annotation.Filterable;
import io.github.tnas.introspectorfilter.exception.ExceptionWrapper;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class TraversalStrategy {

    protected Logger logger = LoggerFactory.getLogger(TraversalStrategy.class);

    protected static final int IDLE_THREAD = 1;
    protected static final int ACTIVE_THREAD = 0;

    protected int heightBound;
    protected int breadthBound;
    protected int numThreads;
    protected ExceptionWrapper wrapper;
    protected Set<Class<? extends Annotation>> hierarchicalAnnotations;
    protected Class<? extends Annotation> relationshipsAnnotation;

    protected final Predicate<Field> isFilterableField = f ->
            Stream.of(f.getAnnotations()).anyMatch(a -> a.annotationType().equals(relationshipsAnnotation));

    protected TraversalStrategy() {
        this.heightBound = Integer.MAX_VALUE;
        this.breadthBound = Integer.MAX_VALUE;
        this.relationshipsAnnotation = Filterable.class;
        this.hierarchicalAnnotations = Collections.emptySet();
        this.wrapper = new ExceptionWrapper();
    }

    public abstract void traverse(Object root, AtomicInteger tidCounter, int[] idleThreads, AtomicBoolean foundValue, String textFilter);

    protected boolean hasActiveThreads(int[] idleThreads, AtomicBoolean foundValue) {
        return Arrays.stream(idleThreads).sum() < this.numThreads && !foundValue.get();
    }

    protected final boolean hasPendingWork(Collection<?> nodesList, AtomicBoolean foundValue) {
        return !nodesList.isEmpty() && !foundValue.get();
    }

    protected boolean isStringOrWrapper(Object fieldValue) {
        return fieldValue instanceof String || ClassUtils.isPrimitiveWrapper(fieldValue.getClass());
    }

    protected boolean containsTextFilter(String text, String filter) {
        return Objects.nonNull(text) && StringUtils.stripAccents(text.toLowerCase()).contains(filter);
    }

    protected void searchInNodeValue(Object nodeValue, String textFilter, AtomicBoolean foundValue, int tid) {
        if (isStringOrWrapper(nodeValue) && containsTextFilter(nodeValue.toString(), textFilter)) {
            foundValue.set(true);
            logger.debug("Thread {} found value '{}'", tid, textFilter);
        }
    }

    protected boolean isValidParentClass(Class<?> parentClass) {
        return Objects.nonNull(parentClass) &&
                (this.hierarchicalAnnotations.isEmpty()
                        || Stream.of(parentClass.getAnnotations())
                        .map(Annotation::annotationType)
                        .anyMatch(this.hierarchicalAnnotations::contains));
    }

    public void setHeightBound(int heightBound) {
        this.heightBound = heightBound;
    }

    public void setBreadthBound(int breadthBound) {
        this.breadthBound = breadthBound;
    }

    public void setNumThreads(int numThreads) {
        this.numThreads = numThreads;
    }

    public void setHierarchicalAnnotations(Set<Class<? extends Annotation>> hierarchicalAnnotations) {
        this.hierarchicalAnnotations = hierarchicalAnnotations;
    }

    public void setRelationshipsAnnotation(Class<? extends Annotation> relationshipsAnnotation) {
        this.relationshipsAnnotation = relationshipsAnnotation;
    }

    public void load(int numThreads) {
        this.numThreads = numThreads;
        this.wrapper = new ExceptionWrapper();
        this.relationshipsAnnotation = Filterable.class;
        this.hierarchicalAnnotations = Collections.emptySet();
    }

}

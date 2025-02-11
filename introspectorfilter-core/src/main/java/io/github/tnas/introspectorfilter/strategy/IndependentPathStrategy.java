package io.github.tnas.introspectorfilter.strategy;

import io.github.tnas.introspectorfilter.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.util.BitSet;
import java.util.Collection;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class IndependentPathStrategy extends TraversalStrategy {

    Logger logger = LoggerFactory.getLogger(IndependentPathStrategy.class);

    private final BiPredicate<Collection<?>, AtomicBoolean> hasPendingWork = (nodesList, foundValue) ->
            !nodesList.isEmpty() && !foundValue.get();

    @Override
    public void traverse(Queue<Node> nodesList, BitSet idleThreads, AtomicBoolean foundValue, String textFilter) {

    }

    public void traverse(Object root, AtomicInteger tidCounter, int[] idleThreads, AtomicBoolean foundValue, String textFilter) {

        final int tid = tidCounter.getAndIncrement();
        logger.debug("Running Thread {}", tid);
        var waitingLogged = false;

        var nodesList = new LinkedList<>();
        nodesList.add(root);

        while (hasActiveThreads.test(idleThreads, foundValue)) {

            while (hasPendingWork.test(nodesList, foundValue)) {

                Object node;

                try {
                    node = nodesList.removeFirst();
                } catch (NoSuchElementException e) {
                    idleThreads[tid] = 1;
                    logger.debug("Thread {} idle", tid);
                    continue;
                }

                idleThreads[tid] = 0;

                logger.debug("Thread {} processing {}", tid, node);

                var nodeValueClass = node.getClass();

                do { // Hierarchical traversing
                    if (Objects.nonNull(this.searchInRelationships(node, nodeValueClass, textFilter, nodesList, foundValue, tid))) {
                        foundValue.set(true);
                        logger.debug("Thread {} found value '{}'", tid, textFilter);
                    }

                    nodeValueClass = nodeValueClass.getSuperclass();
                } while (isValidParentClass(nodeValueClass) && !foundValue.get());

                if (isStringOrWrapper(node) && containsTextFilter(node.toString(), textFilter)) {
                    foundValue.set(true);
                    logger.debug("Thread {} found value '{}'", tid, textFilter);
                }

                idleThreads[tid] = 1;
                logger.debug("Thread {} set idle", tid);
            }

            if (!waitingLogged) {
                logger.debug("Thread {} waiting others to finish: {}", tid, idleThreads);
                waitingLogged = true;
            }
        }
    }

    private Object searchInRelationships(Object node, Class<?> instanceClass, String textFilter,
                                         Collection<Object> nodesList, AtomicBoolean foundValue, int tid) {

        Predicate<Object> matchTextFilter = fieldValue -> {

            if (isStringOrWrapper(fieldValue)) {
                return containsTextFilter(fieldValue.toString(), textFilter);
            } else if (fieldValue instanceof Collection<?> innerCollection) {

                var workInterval = this.getWorkInterval(tid, this.numThreads, innerCollection.size());
                var iterator = innerCollection.iterator();

                // Positioning iterator at from position of work interval
                for (var index = 0; index < workInterval.from && iterator.hasNext(); ++index, iterator.next());

                for (var index = workInterval.from; index < workInterval.to && !foundValue.get() && iterator.hasNext(); ++index) {
                    var element = iterator.next();
                    logger.debug("Thread {} add node {} to list", tid, element);
                    nodesList.add(element);
                }
            } else { // Single class
                nodesList.add(fieldValue);
            }

            return false;
        };

        return Stream.of(instanceClass.getDeclaredFields())
                .filter(isFilterableField)
                .map(this.wrapper.wrap(f -> new PropertyDescriptor(f.getName(), node.getClass()).getReadMethod()
                        .invoke(node)))
                .filter(Objects::nonNull)
                .filter(matchTextFilter)
                .findFirst()
                .orElse(null);
    }

    private WorkInterval getWorkInterval(final int workerId, final int numWorkers, final int workSize) {

        var interval = new WorkInterval();
        var step = workSize / numWorkers;
        interval.from = workerId * step;
        interval.to = workerId == numWorkers - 1 ? workSize : interval.from + step;

        logger.debug("Thread {} get work interval from {} to {}", workerId, interval.from, interval.to);
        return interval;
    }

    private static class WorkInterval {
        private int from;
        private int to;
    }
}

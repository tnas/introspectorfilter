package io.github.tnas.introspectorfilter.strategy;

import io.github.tnas.introspectorfilter.Node;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class FullSharedNodeStrategy extends TraversalStrategy<Queue<Node>> {

    @Override
    public void traverse(Queue<Node> nodesList, AtomicInteger tidCounter, int[] idleThreads, AtomicBoolean foundValue, String textFilter) {

        final int tid = tidCounter.getAndIncrement();
        logger.debug("Running Thread {}", tid);

        while (hasActiveThreads.test(idleThreads, foundValue)) {

            while (hasPendingWork.test(nodesList, foundValue)) { // BFS for relationships

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

        logger.debug("Thread is over");
    }
}

package io.github.tnas.introspectorfilter.strategy;

import io.github.tnas.introspectorfilter.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

public class IndependentPathStrategy extends TraversalStrategy {

    Logger logger = LoggerFactory.getLogger(IndependentPathStrategy.class);

    @Override
    public void traverse(Queue<Node> nodesList, BitSet idleThreads, AtomicBoolean foundValue, String textFilter) {

        final int tid = (int) Thread.currentThread().threadId() % this.numThreads;
        logger.debug("Running Thread");

        var localList = new ArrayList<Node>();

        while (hasActiveThreads.test(idleThreads, foundValue)) {

            while (hasPendingWork.test(nodesList, foundValue)) { // BFS for relationships

                var node = nodesList.poll();

                if (notToProcessNode.test(node)) {
                    idleThreads.set(tid, true);
                    logger.debug("Thread idle");
                    continue;
                }

                idleThreads.set(tid, false);

                assert node != null;
                logger.debug("Processing {}", node.value());

                this.searchInHierarchy(nodesList, node, foundValue, textFilter);

                this.searchInNodeValue(node, textFilter, foundValue);

                idleThreads.set(tid, true);
            }
        }
    }
}

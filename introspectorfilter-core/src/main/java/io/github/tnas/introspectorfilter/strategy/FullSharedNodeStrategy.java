package io.github.tnas.introspectorfilter.strategy;

import io.github.tnas.introspectorfilter.Node;

import java.util.BitSet;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

public class FullSharedNodeStrategy extends TraversalStrategy {

    @Override
    public void traverse(Queue<Node> nodesList, BitSet idleThreads, AtomicBoolean foundValue, String textFilter) {

    }
}

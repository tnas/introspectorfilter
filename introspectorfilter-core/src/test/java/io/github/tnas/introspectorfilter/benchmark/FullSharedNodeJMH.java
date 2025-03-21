package io.github.tnas.introspectorfilter.benchmark;

import io.github.tnas.introspectorfilter.IntrospectorFilter;
import io.github.tnas.introspectorfilter.instance.lieberherr.BusRoute;
import org.instancio.Instancio;
import org.instancio.Select;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.util.List;

@State(Scope.Benchmark)
public class FullSharedNodeJMH {

    private static final int COLLECTION_SIZE = 10;

    private IntrospectorFilter introspectorFilter;
    private BusRoute graph;
    private final String passengerName = "none";

    @Setup(Level.Trial)
    public void setUp() {
        this.introspectorFilter = IntrospectorFilter.builder().build();
        this.graph = Instancio.of(BusRoute.class)
                .generate(Select.all(List.class), gen -> gen.collection().size(COLLECTION_SIZE))
                .create();
    }

    @Fork(value = 1, warmups = 1)
    @Warmup(iterations = 1)
    @Benchmark
    public void not_found_one_instance(Blackhole blackhole) {
        blackhole.consume(this.introspectorFilter.filter(graph, passengerName));
    }
}

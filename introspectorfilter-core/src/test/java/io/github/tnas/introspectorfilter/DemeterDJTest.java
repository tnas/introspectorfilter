package io.github.tnas.introspectorfilter;

import com.github.javafaker.Faker;
import edu.neu.ccs.demeter.dj.ClassGraph;
import edu.neu.ccs.demeter.dj.Strategy;
import edu.neu.ccs.demeter.dj.Traversal;
import io.github.tnas.introspectorfilter.instance.demeterdj.BusRoute;
import io.github.tnas.introspectorfilter.util.FilterVisitor;
import io.github.tnas.introspectorfilter.util.PerformanceLogger;
import io.github.tnas.introspectorfilter.util.TraversalAbortedByFoundValueException;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DemeterDJTest {

    Logger logger = LoggerFactory.getLogger(DemeterDJTest.class);

    private static final int DEFAULT_COLLECTION_SIZE = 100;
    private static final String PKG = "io.github.tnas.introspectorfilter.instance.demeterdj";

    private static Faker faker;

    private Traversal traversal;
    private String passengerName;
    private BusRoute graph;
    private FilterVisitor visitorFilter;
    private Instant start;

    @BeforeAll
    public static void setUpAll() throws IOException {
        faker = new Faker();
        PerformanceLogger.initLogMemory();
        PerformanceLogger.initRuntimeMemory();
    }

    @BeforeEach
    void setUpBeforeEach(TestInfo testInfo) throws IOException {
        this.passengerName = faker.name().name();
        this.traversal = new Traversal(new Strategy(String.format("from %s.BusRoute to %s.Person", PKG, PKG)),
                new ClassGraph(PKG));
        this.visitorFilter = new FilterVisitor(passengerName);
        this.loadGraphModel(testInfo.getDisplayName());
        PerformanceLogger.logMemoryUsage(String.format("[%s - Before]", testInfo.getDisplayName()));
        this.start = Instant.now();
    }

    @AfterEach
    void setUpAfterEach(TestInfo testInfo) throws IOException {
        var end = Instant.now();
        PerformanceLogger.logMemoryUsage(String.format("[%s - After]", testInfo.getDisplayName()));
        var runtimeMessage = String.format("[%s] Graph size: %d, Collections size: %d, Threads: %d, Elapsed time: %d ms%n",
                testInfo.getDisplayName(),
                1,
                DEFAULT_COLLECTION_SIZE,
                1,
                Duration.between(this.start, end).toMillis());
        logger.info(runtimeMessage);
        PerformanceLogger.logElapsedRuntime(runtimeMessage);
    }

    private void loadGraphModel(String testName) {
        this.graph = Instancio.of(BusRoute.class)
                .generate(Select.types(Class::isArray), gen -> gen.array().length(DEFAULT_COLLECTION_SIZE))
                .create();

        if (testName.startsWith("found")) {
            var index = DEFAULT_COLLECTION_SIZE - 1;
            graph.getBuses()[index].getPassengers()[index].setName(passengerName);
        }
    }

    @Test
    void found_lieberherr_one_instance() {
        assertThrows(TraversalAbortedByFoundValueException.class, () -> traversal.traverse(graph, visitorFilter));
    }

    @Test
    void not_found_lieberherr_one_instance() {
        assertFalse((Boolean) traversal.traverse(graph, visitorFilter));
    }
}

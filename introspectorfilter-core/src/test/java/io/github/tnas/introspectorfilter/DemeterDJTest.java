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
import org.instancio.TypeToken;
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
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DemeterDJTest {

    Logger logger = LoggerFactory.getLogger(DemeterDJTest.class);

    private static final int FILTERED_SIZE = 3;
    private static final int DEFAULT_COLLECTION_SIZE = 10;
    private static final String PKG = "io.github.tnas.introspectorfilter.instance.demeterdj";

    private static Faker faker;

    private Traversal traversal;
    private String passengerName;
    private List<BusRoute> graph;
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
        PerformanceLogger.logMemoryUsage(String.format("[%s#%s - Before]", this.getClass().getSimpleName(), testInfo.getDisplayName()));
        this.start = Instant.now();
    }

    @AfterEach
    void setUpAfterEach(TestInfo testInfo) throws IOException {
        var end = Instant.now();
        PerformanceLogger.logMemoryUsage(String.format("[%s#%s - After]", this.getClass().getSimpleName(), testInfo.getDisplayName()));
        var runtimeMessage = String.format("[%s#%s] Graph size: %d, Collections size: %d, Threads: %d, Elapsed time: %d ms%n",
                this.getClass().getSimpleName(),
                testInfo.getDisplayName(),
                1,
                this.getCollectionsSize(),
                1,
                Duration.between(this.start, end).toMillis());
        logger.info(runtimeMessage);
        PerformanceLogger.logElapsedRuntime(runtimeMessage);
    }

    private int getCollectionsSize() {
        return Objects.isNull(System.getProperty("GRAPH_SIZE")) ?
                DEFAULT_COLLECTION_SIZE : Integer.parseInt(System.getProperty("GRAPH_SIZE"));
    }

    private void loadGraphModel(String testName) {

        var collectionsSize = this.getCollectionsSize();

        if (testName.contains("one_instance")) {

            this.graph = List.of(Instancio.of(BusRoute.class)
                    .generate(Select.types(Class::isArray), gen -> gen.array().length(collectionsSize))
                    .create());

            if (testName.startsWith("found")) {
                logger.info("Searching by passenger name '{}'", passengerName);
                var index = collectionsSize - 1;
                graph.getFirst().getBuses()[index].getPassengers()[index].setName(passengerName);
            }

            logger.info("Instance with collections of size {} is ready to test with 1 threads", collectionsSize);
        } else {
            this.graph = Instancio.of(new TypeToken<List<BusRoute>>() { })
                    .generate(Select.root(), gen -> gen.collection().size(collectionsSize))
                    .generate(Select.types(Class::isArray), gen -> gen.array().length(collectionsSize))
                    .create();

            IntStream.rangeClosed(1, FILTERED_SIZE).forEach(i -> {
                var index = collectionsSize - i;
                graph.get(index).getBuses()[index].getPassengers()[index].setName(passengerName);
            });

            logger.info("Collection of size {} is ready to test with 1 threads", collectionsSize);
        }
    }

    @Test
    void found_lieberherr_one_instance() {
        assertEquals(1, graph.stream().filter(o -> {
            try {
                return (boolean) traversal.traverse(o, visitorFilter);
            } catch (TraversalAbortedByFoundValueException e) {
                return true;
            }
        }).count());
    }

    @Test
    void not_found_lieberherr_one_instance() {
        assertEquals(0, graph.stream().filter(o -> (boolean) traversal.traverse(o, visitorFilter)).count());
    }

    @Test
    void found_lieberherr_multiple_instances() {
        assertEquals(FILTERED_SIZE, graph.stream().filter(o -> {
            try {
                return (boolean) traversal.traverse(o, visitorFilter);
            } catch (TraversalAbortedByFoundValueException e) {
                return true;
            }
        }).count());
    }
}

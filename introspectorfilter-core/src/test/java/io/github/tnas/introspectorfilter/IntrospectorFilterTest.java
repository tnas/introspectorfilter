package io.github.tnas.introspectorfilter;

import com.github.javafaker.Faker;
import io.github.tnas.introspectorfilter.instance.lieberherr.BusRoute;
import io.github.tnas.introspectorfilter.strategy.IndependentPathStrategy;
import io.github.tnas.introspectorfilter.util.PerformanceLogger;
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

class IntrospectorFilterTest {

    Logger logger = LoggerFactory.getLogger(IntrospectorFilterTest.class);

    private static final int FILTERED_SIZE = 3;
    private static final int DEFAULT_COLLECTION_SIZE = 10;
    private static final int DEFAULT_NUM_THREADS = 4;

    private static Faker faker;
    private IntrospectorFilter filter;

    private List<BusRoute> graph;
    private String passengerName;
    private Instant start;

    @BeforeAll
    public static void setUpAll() throws IOException {
        faker = new Faker();
        PerformanceLogger.initLogMemory();
        PerformanceLogger.initRuntimeMemory();
    }

    @BeforeEach
    void setUpBeforeEach(TestInfo testInfo) throws IOException {
        this.filter = new IntrospectorFilter();
        this.filter.start(this.getNumThreads());
        this.filter.setTraversalStrategy(new IndependentPathStrategy());
        this.filter.loadStrategy();

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
                this.graph.size(),
                this.getCollectionsSize(),
                this.getNumThreads(),
                Duration.between(this.start, end).toMillis());
        logger.info(runtimeMessage);
        PerformanceLogger.logElapsedRuntime(runtimeMessage);
        this.filter.stop();
    }

    private int getCollectionsSize() {
        return Objects.isNull(System.getProperty("GRAPH_SIZE")) ?
                DEFAULT_COLLECTION_SIZE : Integer.parseInt(System.getProperty("GRAPH_SIZE"));
    }

    private int getNumThreads() {
        return Objects.isNull(System.getProperty("NUM_THREADS")) ?
                DEFAULT_NUM_THREADS : Integer.parseInt(System.getProperty("NUM_THREADS"));
    }

    private void loadGraphModel(String testName) {

        var numThreads = this.getNumThreads();
        var collectionsSize = this.getCollectionsSize();

        filter.start(numThreads);
        this.passengerName = faker.name().name();

        if (testName.contains("one_instance")) {

            this.graph = List.of(Instancio.of(BusRoute.class)
                    .generate(Select.all(List.class), gen -> gen.collection().size(collectionsSize))
                    .create());

            if (testName.startsWith("found")) {
                logger.info("Searching by passenger name '{}'", passengerName);
                var index = collectionsSize - 1;
                graph.getFirst().getBuses().get(index).getPassengers().get(index).setName(passengerName);
            }

            logger.info("Instance with collections of size {} is ready to test with {} threads", collectionsSize, numThreads);
        } else {
            this.graph = Instancio.of(new TypeToken<List<BusRoute>>() { })
                    .generate(Select.root(), gen -> gen.collection().size(collectionsSize))
                    .generate(Select.all(List.class), gen -> gen.collection().size(collectionsSize))
                    .create();

            IntStream.rangeClosed(1, FILTERED_SIZE).forEach(i -> {
                var index = collectionsSize - i;
                graph.get(index).getBuses().get(index).getPassengers().get(index).setName(passengerName);
            });

            logger.info("Collection of size {} is ready to test with {} threads", collectionsSize, numThreads);
        }
    }

    @Test
    void found_lieberherr_one_instance() {
        assertEquals(1, graph.stream().filter(o -> filter.filter(o, passengerName)).count());
    }

    @Test
    void not_found_lieberherr_one_instance() {
        assertEquals(0, graph.stream().filter(o -> filter.filter(o, passengerName)).count());
    }

    @Test
    void found_lieberherr_multiple_instances() {
        assertEquals(FILTERED_SIZE, graph.stream().filter(o -> filter.filter(o, passengerName)).count());
    }
}

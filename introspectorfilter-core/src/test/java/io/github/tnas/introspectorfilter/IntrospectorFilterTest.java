package io.github.tnas.introspectorfilter;

import com.github.javafaker.Faker;
import io.github.tnas.introspectorfilter.lieberherr.BusRoute;
import io.github.tnas.introspectorfilter.util.PerformanceLogger;
import org.instancio.Instancio;
import org.instancio.Select;
import org.instancio.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IntrospectorFilterTest {

    Logger logger = LoggerFactory.getLogger(IntrospectorFilterTest.class);
    private static final int DEFAULT_COLLECTION_SIZE = 300;
    private static final int DEFAULT_NUM_THREADS = 10;

    private static Faker faker;
    private IntrospectorFilter filter;

    private BusRoute graph;
    private String passengerName;
    private Instant start;
    private Instant end;

    @BeforeAll
    public static void setUpAll() throws IOException {
        faker = new Faker();
        PerformanceLogger.initLogMemory();
        PerformanceLogger.initRuntimeMemory();
    }

    @BeforeEach
    void setUpBeforeEach(TestInfo testInfo) throws IOException {
        this.filter = new IntrospectorFilter();
        this.loadGraphModel(testInfo.getDisplayName());
        PerformanceLogger.logMemoryUsage(String.format("[%s - Before]", testInfo.getDisplayName()));
        this.start = Instant.now();
    }

    @AfterEach
    void setUpAfterEach(TestInfo testInfo) throws IOException {
        this.end = Instant.now();
        PerformanceLogger.logMemoryUsage(String.format("[%s - After]", testInfo.getDisplayName()));
        var runtimeMessage = String.format("[%s] Graph size: %d, Collections size: %d, Threads: %d, Elapsed time: %d ms%n",
                testInfo.getDisplayName(),
                1,
                this.getCollectionsSize(),
                this.getNumThreads(),
                Duration.between(this.start, this.end).toMillis());
        logger.info(runtimeMessage);
        PerformanceLogger.logElapsedRuntime(runtimeMessage);
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

        this.graph = Instancio.of(BusRoute.class)
                .generate(Select.all(List.class), gen -> gen.collection().size(collectionsSize))
                .create();

        filter.setNumThreads(numThreads);
        this.passengerName = faker.name().name();

        if (List.of("found_lieberherr_one_instance()").contains(testName)) {
            logger.info("Searching by passenger name '{}'", passengerName);

            var index = collectionsSize - 1;
            graph.getBuses().get(index).getPassengers().get(index).setName(passengerName);
        }

        logger.info("Instance with collections of size {} is ready to test with {} threads", collectionsSize, numThreads);
    }

    @Test
    void found_lieberherr_one_instance() {
        assertEquals(1, Stream.of(graph).filter(o -> filter.filter(o, passengerName)).count());
    }

//    @Test
    void not_found_lieberherr_one_instance() {
        assertEquals(0, Stream.of(graph).filter(o -> filter.filter(o, passengerName)).count());
    }

    @Disabled
    @Test
    void should_success_filter_lieberherr_instance() {

        var collectionsSize = 20;
        var filteredSize = 3;
        var passengerName = faker.name().name();
        logger.debug("Searching by passenger name {}", passengerName);

        var startSetup = Instant.now();
        logger.info("Preparing collection to test ...");

        var graph = Instancio.of(new TypeToken<List<BusRoute>>() { })
                .generate(Select.root(), gen -> gen.collection().size(collectionsSize))
                .generate(Select.all(List.class), gen -> gen.collection().size(collectionsSize))
                .create();

        var endSetup = Instant.now();
        logger.info("Collection to test is ready ({} ms)", Duration.between(startSetup, endSetup).toMillis());

        IntStream.rangeClosed(1, filteredSize).forEach(i -> {
            var index = collectionsSize - i;
            graph.get(index).getBuses().get(index).getPassengers().get(index).setName(passengerName);
        });

        var result = graph.stream()
                .filter(r -> {
                    boolean found;
                    var start = Instant.now();
                    found = filter.filter(r, passengerName);
                    var end = Instant.now();
                    logger.info("Processing {}: elapsed time: {} ms", r, Duration.between(start, end).toMillis());
                    return found;
                })
                .toList();

        assertEquals(filteredSize, result.size());
    }
}

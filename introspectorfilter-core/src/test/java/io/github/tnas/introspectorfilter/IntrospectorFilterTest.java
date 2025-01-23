package io.github.tnas.introspectorfilter;

import com.github.javafaker.Faker;
import io.github.tnas.introspectorfilter.lieberherr.BusRoute;
import org.instancio.Instancio;
import org.instancio.Select;
import org.instancio.TypeToken;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IntrospectorFilterTest {

    Logger logger = LoggerFactory.getLogger(IntrospectorFilterTest.class);

    private static IntrospectorFilter filter;
    private static Faker faker;

    @BeforeAll
    public static void setUp() {
        filter = new IntrospectorFilter();
        faker = new Faker();
    }

    @Test
    void should_success_no_filter_lieberherr_one_instance() {

        var filteredSize = 0;
        var collectionsSize = 300;
        var passengerName = faker.name().name();
        logger.info("Searching by passenger name {}", passengerName);

        var startSetup = Instant.now();
        logger.info("Preparing collection to test ...");

        var graph = Instancio.of(BusRoute.class)
                .generate(Select.all(List.class), gen -> gen.collection().size(collectionsSize))
                .create();

        var endSetup = Instant.now();
        logger.info("Collection to test is ready ({} ms)", Duration.between(startSetup, endSetup).toMillis());

        var result = Stream.of(graph)
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

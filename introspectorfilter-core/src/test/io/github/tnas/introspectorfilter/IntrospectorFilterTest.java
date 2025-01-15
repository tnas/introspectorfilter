package io.github.tnas.introspectorfilter;

import com.github.javafaker.Faker;
import io.github.tnas.introspectorfilter.lieberherr.BusRoute;
import org.instancio.Instancio;
import org.instancio.Select;
import org.instancio.TypeToken;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IntrospectorFilterTest {

    private static IntrospectorFilter filter;
    private static Faker faker;
    private static Random random;

    @BeforeAll
    public static void setUp() {
        filter = new IntrospectorFilter();
        faker = new Faker();
        random = new Random();
    }

    @Test
    void should_success_filter_lieberherr_instance() {

        var collectionsSize = 10;
        var filteredSize = 3;
        var passengerName = faker.name().name();

        var graph = Instancio.of(new TypeToken<List<BusRoute>>() { })
                .generate(Select.root(), gen -> gen.collection().size(collectionsSize))
                .generate(Select.all(List.class), gen -> gen.collection().size(collectionsSize))
                .create();

        IntStream.range(0, filteredSize).forEach(i -> {
            graph.get(random.nextInt(0, collectionsSize))
                    .getBuses().get(random.nextInt(0, collectionsSize))
                    .getPassengers().get(random.nextInt(0, collectionsSize))
                    .setName(passengerName);
        });

        var result = graph.stream().filter(r -> filter.filter(r, passengerName)).toList();
        assertEquals(filteredSize, result.size());
    }
}

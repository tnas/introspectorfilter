package io.github.tnas.introspectorfilter;

import com.github.javafaker.Faker;
import edu.neu.ccs.demeter.dj.ClassGraph;
import edu.neu.ccs.demeter.dj.Strategy;
import edu.neu.ccs.demeter.dj.Traversal;
import edu.neu.ccs.demeter.dj.Visitor;
import io.github.tnas.introspectorfilter.lieberherr.demeterdj.BusRoute;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.Test;

class DemeterDJTest {

    private static final int DEFAULT_COLLECTION_SIZE = 5;
    private static final String PKG = "io.github.tnas.introspectorfilter.lieberherr.demeterdj";

    @Test
    void found_lieberherr_one_instance() {

        var faker = new Faker();
        var passengerName = faker.name().name();
        System.out.println("Searching by: " + passengerName);

        var graph = Instancio.of(BusRoute.class)
                .generate(Select.types(Class::isArray), gen -> gen.array().length(DEFAULT_COLLECTION_SIZE))
                .create();

        var index = DEFAULT_COLLECTION_SIZE - 1;
        graph.getBuses()[index].getPassengers()[index].setName(passengerName);

        var classGraph = new ClassGraph(PKG);
        var strategy = new Strategy(String.format("from %s.BusRoute to %s.Person", PKG, PKG));
        var traversal = new Traversal(strategy, classGraph);
        var visitorFilter = new FilterVisitor(passengerName);

        var outcome = traversal.traverse(graph, visitorFilter);

        System.out.println("Outcome: " + outcome);
    }
}

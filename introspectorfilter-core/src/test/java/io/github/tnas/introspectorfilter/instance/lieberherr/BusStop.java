package io.github.tnas.introspectorfilter.instance.lieberherr;

import io.github.tnas.introspectorfilter.annotation.Filterable;

import java.util.List;

public class BusStop {

    private static int busCount;

    private final int id;

    public BusStop() {
        this.id = busCount++;
    }

    @Filterable
    private List<Person> waiting;

    public List<Person> getWaiting() {
        return waiting;
    }

    public void setWaiting(List<Person> waiting) {
        this.waiting = waiting;
    }

    @Override
    public String toString() {
        return "BusStop{" +
                "id=" + id +
                '}';
    }
}

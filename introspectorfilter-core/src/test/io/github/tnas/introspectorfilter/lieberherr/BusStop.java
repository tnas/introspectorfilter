package io.github.tnas.introspectorfilter.lieberherr;

import io.github.tnas.introspectorfilter.annotation.Filterable;

import java.util.List;

public class BusStop {

    @Filterable
    private List<Person> waiting;

    public List<Person> getWaiting() {
        return waiting;
    }

    public void setWaiting(List<Person> waiting) {
        this.waiting = waiting;
    }
}

package io.github.tnas.introspectorfilter.instance.demeterdj;

import io.github.tnas.introspectorfilter.annotation.Filterable;

public class BusStop {

    private static int busCount;

    private final int id;

    public BusStop() {
        this.id = busCount++;
    }

    @Filterable
    private Person[] waiting;

    public Person[] getWaiting() {
        return waiting;
    }

    public void setWaiting(Person[] waiting) {
        this.waiting = waiting;
    }

    @Override
    public String toString() {
        return "BusStop{" +
                "id=" + id +
                '}';
    }
}

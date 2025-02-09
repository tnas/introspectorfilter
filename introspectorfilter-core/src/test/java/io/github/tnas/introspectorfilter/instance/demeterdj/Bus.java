package io.github.tnas.introspectorfilter.lieberherr.demeterdj;

import io.github.tnas.introspectorfilter.annotation.Filterable;

public class Bus {

    @Filterable
    private String name;

    private int capacity;

    @Filterable
    private Person[]passengers;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Person[] getPassengers() {
        return passengers;
    }

    public void setPassengers(Person[] passengers) {
        this.passengers = passengers;
    }

    @Override
    public String toString() {
        return "Bus{" +
                "name='" + name + '\'' +
                '}';
    }
}

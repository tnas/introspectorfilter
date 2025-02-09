package io.github.tnas.introspectorfilter.lieberherr;

import io.github.tnas.introspectorfilter.annotation.Filterable;

import java.util.List;

public class Bus {

    @Filterable
    private String name;

    private int capacity;

    @Filterable
    private List<Person> passengers;

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

    public List<Person> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<Person> passengers) {
        this.passengers = passengers;
    }

    @Override
    public String toString() {
        return "Bus{" +
                "name='" + name + '\'' +
                '}';
    }
}

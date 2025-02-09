package io.github.tnas.introspectorfilter.instance.demeterdj;

import io.github.tnas.introspectorfilter.annotation.Filterable;

public class Person {

    @Filterable
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                '}';
    }
}

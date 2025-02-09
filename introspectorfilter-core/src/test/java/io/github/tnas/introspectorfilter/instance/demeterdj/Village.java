package io.github.tnas.introspectorfilter.instance.demeterdj;

import io.github.tnas.introspectorfilter.annotation.Filterable;

public class Village {

    @Filterable
    private String name;

    @Filterable
    private BusStop[] busStops;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BusStop[] getBusStops() {
        return busStops;
    }

    public void setBusStops(BusStop[] busStops) {
        this.busStops = busStops;
    }

    @Override
    public String toString() {
        return "Village{" +
                "name='" + name + '\'' +
                '}';
    }
}

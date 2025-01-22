package io.github.tnas.introspectorfilter.lieberherr;

import io.github.tnas.introspectorfilter.annotation.Filterable;

import java.util.List;

public class Village {

    @Filterable
    private String name;

    @Filterable
    private List<BusStop> busStops;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BusStop> getBusStops() {
        return busStops;
    }

    public void setBusStops(List<BusStop> busStops) {
        this.busStops = busStops;
    }
}

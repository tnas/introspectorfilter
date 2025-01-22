package io.github.tnas.introspectorfilter.lieberherr;

import io.github.tnas.introspectorfilter.annotation.Filterable;

import java.util.List;

public class BusRoute {

    @Filterable
    private List<Bus> buses;

    @Filterable
    private List<Village> villages;

    public List<Bus> getBuses() {
        return buses;
    }

    public void setBuses(List<Bus> buses) {
        this.buses = buses;
    }

    public List<Village> getVillages() {
        return villages;
    }

    public void setVillages(List<Village> villages) {
        this.villages = villages;
    }
}

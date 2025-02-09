package io.github.tnas.introspectorfilter.lieberherr.demeterdj;

import io.github.tnas.introspectorfilter.annotation.Filterable;

public class BusRoute {

    private static int busRoute;

    private final int id;

    public BusRoute() {
        this.id = busRoute++;
    }

    @Filterable
    private Bus[] buses;

    @Filterable
    private Village[] villages;

    public static int getBusRoute() {
        return busRoute;
    }

    public static void setBusRoute(int busRoute) {
        BusRoute.busRoute = busRoute;
    }

    public int getId() {
        return id;
    }

    public Bus[] getBuses() {
        return buses;
    }

    public void setBuses(Bus[] buses) {
        this.buses = buses;
    }

    public Village[] getVillages() {
        return villages;
    }

    public void setVillages(Village[] villages) {
        this.villages = villages;
    }

    @Override
    public String toString() {
        return "BusRoute{" +
                "id=" + id +
                '}';
    }
}

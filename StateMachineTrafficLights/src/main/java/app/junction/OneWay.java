package app.junction;

import app.road.Road;

import java.util.ArrayList;
import java.util.List;

/**
 * A OneWay is a Junction with one in-Road and one out-Road.
 */
public class OneWay implements Junction {
    private ArrayList<Road> inRoads = new ArrayList<>();
    private ArrayList<Road> outRoads = new ArrayList<>();

    @Override
    public void addInRoad(Road road) {
        this.inRoads = new ArrayList<>();
        this.inRoads.add(road);
    }

    @Override
    public void addOutRoad(Road road) {
        this.outRoads = new ArrayList<>();
        this.outRoads.add(road);
    }

    @Override
    public List<Road> getInRoads() {
        return this.inRoads;
    }

    @Override
    public List<Road> getOutRoads() {
        return this.outRoads;
    }
}

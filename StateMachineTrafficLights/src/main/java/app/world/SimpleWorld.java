package app.world;

import app.junction.OneWay;
import app.road.CarSource;
import app.road.Road;
import app.road.SimpleRoad;
import app.road.Sink;

import java.util.ArrayList;
import java.util.List;

public class SimpleWorld implements WorldBuilder {
    @Override
    public List<Road> getRoads() {
        ArrayList<Road> roads = new ArrayList<>();

        OneWay first = new OneWay();
        OneWay second = new OneWay();

        roads.add(new CarSource(first, 4));
        roads.add(new SimpleRoad(20, first, second));
//        roads.add(new Sink(second));

        return roads;
    }
}

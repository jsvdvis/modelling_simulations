package legacyexperiment.simulations;

import legacyexperiment.model.junction.OneWay;
import legacyexperiment.model.road.CarSource;
import legacyexperiment.model.road.Road;
import legacyexperiment.model.road.SimpleRoad;

import java.util.ArrayList;
import java.util.List;

public class SimpleWorld implements Simulation {
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

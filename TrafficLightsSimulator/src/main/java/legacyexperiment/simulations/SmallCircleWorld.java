package legacyexperiment.simulations;

import legacyexperiment.model.junction.OneWay;
import legacyexperiment.model.road.Road;
import legacyexperiment.model.road.SimpleRoad;
import legacyexperiment.model.road_user.Car;

import java.util.ArrayList;
import java.util.List;

public class SmallCircleWorld implements Simulation {
    @Override
    public List<Road> getRoads() {
        ArrayList<Road> roads = new ArrayList<>();

        OneWay first = new OneWay();
        OneWay second = new OneWay();

        Road firstRoad = new SimpleRoad(20, first, second);
        firstRoad.enqueueRoadUser(new Car(3));

        roads.add(firstRoad);
        roads.add(new SimpleRoad(25, second, first));

        return roads;
    }
}

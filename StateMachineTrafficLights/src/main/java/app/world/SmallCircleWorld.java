package app.world;

import app.junction.OneWay;
import app.road.Road;
import app.road.SimpleRoad;
import app.road_user.Car;

import java.util.ArrayList;
import java.util.List;

public class SmallCircleWorld implements WorldBuilder {
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

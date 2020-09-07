package app.road;

import app.junction.Junction;
import app.road_user.RoadUser;

import java.util.LinkedList;

/**
 * Road that acts as a Sink for RoadUsers.
 */
public class Sink implements Road {
    public Sink(Junction startPoint) {
        startPoint.addOutRoad(this);
    }

    @Override
    public Junction getEndPoint() {
        return null;
    }

    @Override
    public LinkedList<RoadUser> getRoadUsers() {
        return new LinkedList<>();
    }

    @Override
    public void enqueueRoadUser(RoadUser roadUser) {
    }

    @Override
    public void dequeueRoadUser() {
    }

    @Override
    public int getRoadLength() {
        return 0;
    }

    @Override
    public int getFreeSpaceForRoadUser(RoadUser roadUser) {
        return 10000;
    }
}

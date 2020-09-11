package legacyexperiment.model.road;

import legacyexperiment.model.junction.Junction;
import legacyexperiment.model.road_user.RoadUser;

import java.util.LinkedList;

/**
 * A Road is a connection between two Junctions.
 * It has a queue (implemented using LinkedList) of RoadUsers, a certain length
 * and free space.
 */
public interface Road {
    public Junction getEndPoint();

    public LinkedList<RoadUser> getRoadUsers();
    public void enqueueRoadUser(RoadUser roadUser);
    public void dequeueRoadUser();

    public int getRoadLength();
    public int getFreeSpaceForRoadUser(RoadUser roadUser);
}

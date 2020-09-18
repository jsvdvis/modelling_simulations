package legacyexperiment.model.road;

import legacyexperiment.model.junction.Junction;
import legacyexperiment.model.road_user.RoadUser;

import java.util.LinkedList;

/**
 * Basic implementation of a Road.
 */
public class SimpleRoad implements Road {
    private final int roadLength;
    private final Junction startPoint;
    private final Junction endPoint;
    private final LinkedList<RoadUser> roadUsers = new LinkedList<>();

    public SimpleRoad(int roadLength, Junction startPoint, Junction endPoint) {
        this.roadLength = roadLength;

        this.startPoint = startPoint;
        startPoint.addOutRoad(this);

        this.endPoint = endPoint;
        endPoint.addInRoad(this);
    }

    public Junction getStartPoint() {
        return this.startPoint;
    }

    @Override
    public Junction getEndPoint() {
        return this.endPoint;
    }

    @Override
    public LinkedList<RoadUser> getRoadUsers() {
        return this.roadUsers;
    }

    @Override
    public void enqueueRoadUser(RoadUser roadUser) {
        roadUser.setCurrentRoad(this);
        this.roadUsers.add(roadUser);
    }

    @Override
    public void dequeueRoadUser() {
        this.roadUsers.remove();
    }

    @Override
    public int getRoadLength() {
        return this.roadLength;
    }

    @Override
    public int getFreeSpaceForRoadUser(RoadUser roadUser) {
        int freeSpace = this.getRoadLength();
        int roadUserIndex = this.roadUsers.indexOf(roadUser);
        if (roadUserIndex != -1) {
            // User is already on road, first assume it's in the front.
            freeSpace = roadUser.getRoadPosition();
            if (roadUserIndex > 0) {
                // It's not, actually, find end of next user.
                RoadUser nextRoadUser = this.roadUsers.get(roadUserIndex - 1);
                freeSpace -= (nextRoadUser.getRoadPosition() + nextRoadUser.getOccupiedSpace());
            }
        } else if (!this.roadUsers.isEmpty()) {
            // User is not on road, but there are others, find end of last user on road.
            RoadUser lastRoadUser = this.roadUsers.getLast();
            freeSpace -= (lastRoadUser.getRoadPosition() + lastRoadUser.getOccupiedSpace());
        }
        return freeSpace;
    }
}

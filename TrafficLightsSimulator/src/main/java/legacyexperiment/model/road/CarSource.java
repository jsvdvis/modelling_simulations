package legacyexperiment.model.road;

import legacyexperiment.model.junction.Junction;
import legacyexperiment.model.road_user.Car;
import legacyexperiment.model.road_user.RoadUser;

import java.util.LinkedList;

/**
 * Road that acts as a source of Cars.
 */
public class CarSource implements Road {
    private final Junction endPoint;
    private final LinkedList<RoadUser> roadUsers = new LinkedList<>();
    private final int carSize;

    public CarSource(Junction endPoint, int carSize) {
        this.endPoint = endPoint;
        endPoint.addInRoad(this);
        this.carSize = carSize;
    }

    @Override
    public Junction getEndPoint() {
        return this.endPoint;
    }

    @Override
    public LinkedList<RoadUser> getRoadUsers() {
        if (this.roadUsers.isEmpty()) {
            this.enqueueRoadUser(new Car(this.carSize));
        }
        return roadUsers;
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
        return 0;
    }

    @Override
    public int getFreeSpaceForRoadUser(RoadUser roadUser) {
        return 0;
    }
}

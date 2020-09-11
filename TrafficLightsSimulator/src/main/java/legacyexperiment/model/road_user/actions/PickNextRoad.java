package legacyexperiment.model.road_user.actions;

import legacyexperiment.model.road.Road;
import legacyexperiment.model.road_user.RoadUser;
import legacyexperiment.model.statemachine.Action;

import java.util.List;

/**
 * Action for changing the Road a RoadUser is moving on.
 */
public class PickNextRoad implements Action {
    private final RoadUser roadUser;
    private final Road currentRoad;
    private final Road nextRoad;

    public PickNextRoad(RoadUser roadUser) {
        this.roadUser = roadUser;
        this.currentRoad = roadUser.getCurrentRoad();
        this.nextRoad = this.getNextRoad(this.currentRoad);
    }

    @Override
    public void perform() {
        int roadFreeSpace = this.roadUser.getCurrentRoadFreeSpace();
        int nextRoadFreeSpace = this.nextRoad.getFreeSpaceForRoadUser(this.roadUser);
        int nextRoadMovement = Math.min(
                this.roadUser.getMovingVelocity() - roadFreeSpace,
                nextRoadFreeSpace
        );
        this.currentRoad.dequeueRoadUser();
        this.roadUser.setRoadPosition(this.nextRoad.getRoadLength() - nextRoadMovement);
        this.nextRoad.enqueueRoadUser(this.roadUser);

        System.out.println(
                "Move "
                        + this.roadUser.toString()
                        + " from "
                        + this.currentRoad.toString()
                        + " to "
                        + this.nextRoad
                        + " moving to "
                        + this.roadUser.getRoadPosition()
        );
    }

    @Override
    public boolean canPerform() {
        if (
                this.nextRoad == null
                || this.roadUser.getCurrentRoadFreeSpace() < this.roadUser.getRoadPosition()
                || this.nextRoad.getFreeSpaceForRoadUser(this.roadUser) <= 0
        ) {
            return false;
        }

        return this.roadUser.getCurrentRoadFreeSpace() < this.roadUser.getMovingVelocity();
    }

    private Road getNextRoad(Road currentRoad) {
        List<Road> outRoads = currentRoad.getEndPoint().getOutRoads();
        if (outRoads.isEmpty()) {
            return null;
        }
        // Just getting the first one for now, should probably be based on some
        // kind of strategy, e.g. based on a predefined path or randomly
        // picked.
        return outRoads.get(0);
    }
}

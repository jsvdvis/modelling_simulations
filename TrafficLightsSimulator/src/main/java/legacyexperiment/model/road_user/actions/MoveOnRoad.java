package legacyexperiment.model.road_user.actions;

import legacyexperiment.model.road_user.RoadUser;
import legacyexperiment.model.statemachine.Action;

public class MoveOnRoad implements Action {
    private final RoadUser roadUser;

    public MoveOnRoad(RoadUser roadUser) {
        this.roadUser = roadUser;
    }

    @Override
    public void perform() {
        int movement = Math.min(
                this.roadUser.getCurrentRoadFreeSpace(),
                this.roadUser.getMovingVelocity()
        );
        int newPosition = this.roadUser.getRoadPosition() - movement;
        this.roadUser.setRoadPosition(newPosition);

        System.out.println(
                "Let "
                + this.roadUser.toString()
                + " move to "
                + newPosition
        );
    }

    @Override
    public boolean canPerform() {
        return Math.min(
                this.roadUser.getCurrentRoadFreeSpace(),
                this.roadUser.getMovingVelocity()
        ) > 0;
    }
}

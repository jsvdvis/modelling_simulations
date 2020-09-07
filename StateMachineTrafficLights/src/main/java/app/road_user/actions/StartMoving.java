package app.road_user.actions;

import app.road_user.RoadUser;
import app.road_user.RoadUserStateType;
import app.state_machine.Action;

/**
 * Action to start moving a RoadUser. Can only be executed when there is free
 * space in front of the RoadUser, either on the current road or on the next.
 */
public class StartMoving implements Action {
    private final RoadUser roadUser;

    public StartMoving(RoadUser roadUser) {
        this.roadUser = roadUser;
    }

    @Override
    public void perform() {
        System.out.println(
                "Get "
                + this.roadUser.toString()
                + " ready to move..."
        );
        this.roadUser.setStateType(RoadUserStateType.MOVING);
    }

    @Override
    public boolean canPerform() {
        if (Math.min(
                this.roadUser.getCurrentRoadFreeSpace(),
                this.roadUser.getMovingVelocity()
        ) > 0) {
            return true;
        }

        Action action = new PickNextRoad(this.roadUser);
        return action.canPerform();
    }
}

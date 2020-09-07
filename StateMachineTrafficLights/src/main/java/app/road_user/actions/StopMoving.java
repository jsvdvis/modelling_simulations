package app.road_user.actions;

import app.road_user.RoadUser;
import app.road_user.RoadUserStateType;
import app.state_machine.Action;

/**
 * Action to stop the specified RoadUser from moving, can always be executed.
 */
public class StopMoving implements Action {
    private final RoadUser roadUser;

    public StopMoving(RoadUser roadUser) {
        this.roadUser = roadUser;
    }

    @Override
    public void perform() {
        System.out.println(
                "Stop "
                + this.roadUser.toString()
                + " user from moving..."
        );
        this.roadUser.setStateType(RoadUserStateType.STATIONARY);
    }

    @Override
    public boolean canPerform() {
        return true;
    }
}

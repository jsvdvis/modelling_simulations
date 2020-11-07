package legacyexperiment.model.road_user.actions;

import legacyexperiment.model.road_user.RoadUser;
import legacyexperiment.model.road_user.RoadUserStateType;
import legacyexperiment.model.statemachine.Action;

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

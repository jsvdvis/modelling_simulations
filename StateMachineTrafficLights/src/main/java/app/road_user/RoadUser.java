package app.road_user;

import app.road.Road;
import app.state_machine.HasState;

/**
 * A RoadUser is someone/something that can move on a Road. It occupies
 * a certain space and moves with a certain velocity. It also can act, based
 * on its current state.
 */
public interface RoadUser extends HasState<RoadUserStateType> {
    public int getMovingVelocity();
    public int getOccupiedSpace();

    public int getRoadPosition();
    public void setRoadPosition(int roadPosition);

    public Road getCurrentRoad();
    public void setCurrentRoad(Road road);
    public int getCurrentRoadFreeSpace();
}

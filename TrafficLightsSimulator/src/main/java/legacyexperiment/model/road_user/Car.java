package legacyexperiment.model.road_user;

import legacyexperiment.model.road.Road;
import legacyexperiment.model.road_user.actions.MoveOnRoad;
import legacyexperiment.model.road_user.actions.PickNextRoad;
import legacyexperiment.model.road_user.actions.StartMoving;
import legacyexperiment.model.road_user.actions.StopMoving;
import legacyexperiment.model.statemachine.StatefullObject;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * First basic RoadUser.
 */
public class Car extends StatefullObject<RoadUserStateType> implements RoadUser {
    private int roadPosition;
    private Road currentRoad;
    private final int movingVelocity;
    private final int occupiedSpace;

    public Car(int movingVelocity) {
        this(0, movingVelocity, 2, RoadUserStateType.MOVING);
    }

    public Car(
            int roadPosition,
            int movingVelocity,
            int occupiedSpace,
            RoadUserStateType roadUserStateType
    ) {
        super(roadUserStateType);
        this.roadPosition = roadPosition;
        this.movingVelocity = movingVelocity;
        this.occupiedSpace = occupiedSpace;
    }

    @Override
    public int getMovingVelocity() {
        return this.movingVelocity;
    }

    @Override
    public int getOccupiedSpace() {
        return this.occupiedSpace;
    }

    @Override
    public int getRoadPosition() {
        return this.roadPosition;
    }

    @Override
    public void setRoadPosition(int roadPosition) {
        this.roadPosition = roadPosition;
    }

    @Override
    public Road getCurrentRoad() {
        return this.currentRoad;
    }

    @Override
    public void setCurrentRoad(Road road) {
        this.currentRoad = road;
    }

    @Override
    public int getCurrentRoadFreeSpace() {
        return this.currentRoad.getFreeSpaceForRoadUser(this);
    }

    @Override
    protected HashMap<RoadUserStateType, List<Constructor<?>>> getAllActionConstructorsPerStates() {
        HashMap<RoadUserStateType, List<Constructor<?>>> actionConstructors = new HashMap<>();
        try {
            // Actions for RoadUserStateType.MOVING
            ArrayList<Constructor<?>> movingActions = new ArrayList<>();
            movingActions.add(
                    // Picks the next road to move to.
                    Class
                            .forName(PickNextRoad.class.getName())
                            .getConstructor(RoadUser.class)
            );
            movingActions.add(
                    // Moves on the current road.
                    Class
                            .forName(MoveOnRoad.class.getName())
                            .getConstructor(RoadUser.class)
            );
            movingActions.add(
                    // Stops moving on the current road.
                    Class
                            .forName(StopMoving.class.getName())
                            .getConstructor(RoadUser.class)
            );
            actionConstructors.put(RoadUserStateType.MOVING, movingActions);

            // Actions for RoadUserStateType.STATIONARY
            ArrayList<Constructor<?>> stationaryActions = new ArrayList<>();
            stationaryActions.add(
                    // Starts moving on the current road.
                    Class
                            .forName(StartMoving.class.getName())
                            .getConstructor(RoadUser.class)
            );
            actionConstructors.put(RoadUserStateType.STATIONARY, stationaryActions);
        } catch (ClassNotFoundException|NoSuchMethodException exception) {
            exception.printStackTrace();
        }
        return actionConstructors;
    }
}

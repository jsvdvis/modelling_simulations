package legacyexperiment.simulator;

import legacyexperiment.model.road.Road;
import legacyexperiment.model.road_user.RoadUser;
import legacyexperiment.model.statemachine.Action;
import legacyexperiment.simulations.Simulation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class Simulator {
    private final List<Road> roads;

    public Simulator(Simulation simulation) {
        this.roads = simulation.getRoads();
    }
    
    public void run() {
        try {
            int iteration = 0;
            do {
                iteration += 1;
                System.out.println("<<Step " + iteration + ">>");
            } while (this.step());
            System.out.println("No actions, aborting.");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Iterates over all RoadUsers on all Roads, finds the first possible
     * Action to perform for each. Each Action is the performed.
     * @return boolean True when one or more Action was executed, false if
     * none where possible.
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    private boolean step() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        ArrayList<Action> actions = new ArrayList<>();

        for (Road road : this.roads) {
            for (RoadUser roadUser : road.getRoadUsers()) {
                // Iterate over possible actions in the current state of the
                // RoadUser, add first Action that can be performed to the list.
                List<Constructor<?>> possibleActions = roadUser.getActionsBasedOnState();
                for (Constructor<?> constructor : possibleActions) {
                    Action action = (Action) constructor.newInstance(roadUser);
                    if (action.canPerform()) {
                        actions.add(action);
                        break;
                    }
                }
            }
        }
        
        actions.forEach(Action::perform);
        
        return !actions.isEmpty();
    }
}
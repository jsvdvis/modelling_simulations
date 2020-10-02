package nl.rug.modellingsimulations.model.trafficlight.trafficlightstrategy;

import nl.rug.modellingsimulations.config.TrafficLightConfig;
import nl.rug.modellingsimulations.model.navigablenode.JunctionLaneNavigableNode;
import nl.rug.modellingsimulations.model.trafficlight.TrafficLightJunction;

import java.util.LinkedList;
import java.util.List;

public class TimedSidedRoundRobinTrafficLightStrategy implements TrafficLightStrategy {

    private final TrafficLightJunction trafficLightJunction;
    private LinkedList<List<JunctionLaneNavigableNode>> lanePerSideQueue = new LinkedList<>();
    int timeLeft = 0;

    public TimedSidedRoundRobinTrafficLightStrategy(TrafficLightJunction trafficLightJunction) {
        this.trafficLightJunction = trafficLightJunction;
    }

    @Override
    public void updateTrafficLights() {
        if(timeLeft == 0) {
            // Step 1: Set all lights to red
            this.trafficLightJunction.setAllTrafficLightsToRed();

            // Step 2: Get the next group of lanes on the same side
            List<JunctionLaneNavigableNode> lanesOnSameSideList = lanePerSideQueue.pop();

            // Step 3: Set the lights to green for all lanes on that same side
            lanesOnSameSideList.forEach(lane -> lane.setGreenLight(true));

            // Step 4: Put the list back on the queue, since we are using round robin.
            lanePerSideQueue.add(lanesOnSameSideList);

            // Step 5: Set the delay to not change the lights
            timeLeft = TrafficLightConfig.getMinimumTimeGreenLight();
        }
        timeLeft--;
    }

    @Override
    public void initialize() {
        // Get all lanes
        this.lanePerSideQueue.addAll(this.trafficLightJunction.getLanesSameJunctionSideGroups());
    }

}

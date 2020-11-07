package nl.rug.modellingsimulations.model.trafficlight.trafficlightstrategy;

import nl.rug.modellingsimulations.config.TrafficLightConfig;
import nl.rug.modellingsimulations.model.navigablenode.JunctionLaneNavigableNode;
import nl.rug.modellingsimulations.model.trafficlight.TrafficLightJunction;

import java.util.LinkedList;
import java.util.List;

public class SensoredSidedRoundRobinTrafficLightStrategy implements TrafficLightStrategy {

    private final TrafficLightJunction trafficLightJunction;
    private final LinkedList<List<JunctionLaneNavigableNode>> lanePerSideQueue = new LinkedList<>();
    int timeLeft = 0;

    public SensoredSidedRoundRobinTrafficLightStrategy(TrafficLightJunction trafficLightJunction) {
        this.trafficLightJunction = trafficLightJunction;
    }

    @Override
    public void updateTrafficLights() {
        if(timeLeft != 0) {
            // Set the timeLeft to 0 when there are no longer any vehicles waiting
            long vehiclesWaiting = lanePerSideQueue.getLast().stream().filter(x -> x.getTrafficLoad() > 0.00001).count();
            if(vehiclesWaiting == 0)
                timeLeft = 0;
        }

        if(timeLeft == 0) {
            // Step 1: Get the next group of lanes on the same side
            int i = 0;
            List<JunctionLaneNavigableNode> lanesOnSameSideList = null;
            while(i < lanePerSideQueue.size()) {
                lanesOnSameSideList = lanePerSideQueue.remove();
                lanePerSideQueue.add(lanesOnSameSideList);

                long vehiclesWaiting = lanesOnSameSideList.stream().filter(x -> x.getTrafficLoad() > 0.00001).count();
                if(vehiclesWaiting > 0.0000001)
                    break;

                i++;
            }

            // Step 2: Return if there are no vehicles in any of the lanes
            if(i == lanePerSideQueue.size())
                return;

            // Step 3: Set all lights to red
            this.trafficLightJunction.setAllTrafficLightsToRed();

            // Step 4: Set the lights to green for all lanes on that same side
            lanesOnSameSideList.forEach(lane -> lane.setGreenLight(true));

            // Step 5: Put the list back on the queue, since we are using round robin.
            lanePerSideQueue.add(lanesOnSameSideList);

            // Step 6: Set the delay to not change the lights
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

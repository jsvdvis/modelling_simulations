package nl.rug.modellingsimulations.model.trafficlight.trafficlightstrategy;

import nl.rug.modellingsimulations.config.TrafficLightConfig;
import nl.rug.modellingsimulations.model.navigablenode.JunctionLaneNavigableNode;
import nl.rug.modellingsimulations.model.trafficlight.TrafficLightJunction;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SensoredRoundRobinTrafficLightStrategy implements TrafficLightStrategy {

    private final TrafficLightJunction trafficLightJunction;
    private final LinkedHashSet<JunctionLaneNavigableNode> redLightsQueue = new LinkedHashSet<>();
    private Map<JunctionLaneNavigableNode, Integer> greenLanesTime = new HashMap<>();

    public SensoredRoundRobinTrafficLightStrategy(TrafficLightJunction trafficLightJunction) {
        this.trafficLightJunction = trafficLightJunction;
    }

    @Override
    public void updateTrafficLights() {
        // STEP 1: A step has passed, so decrement the timeout
        greenLanesTime = greenLanesTime.entrySet().parallelStream()
                .collect(Collectors.toMap(Map.Entry::getKey, laneTime -> laneTime.getValue()-1));

        // STEP 2: For all green lights with no vehicles waiting, set the time waiting to 0
        greenLanesTime.entrySet().parallelStream()
                .filter(greenLaneTime -> greenLaneTime.getKey().getTrafficLoad() < 0.0001)
                .forEach(greenLaneTime -> {
                    greenLanesTime.put(greenLaneTime.getKey(), 0);
                });

        // STEP 3: For all green lights with a timeout == 0, turn the light to red.
        for (Map.Entry<JunctionLaneNavigableNode, Integer> greenLaneTime : new ArrayList<>(greenLanesTime.entrySet())) {
            if (greenLaneTime.getValue() == 0) {
                greenLanesTime.remove(greenLaneTime.getKey());
                greenLaneTime.getKey().setGreenLight(false);
                redLightsQueue.add(greenLaneTime.getKey());
            }
        }

        // STEP 4: Put red traffic lights with no traffic at the end of the queue,
        // such that longer waiting lights get processed first
        for (JunctionLaneNavigableNode junctionLaneNavigableNode : new ArrayList<>(redLightsQueue)) {
            if (junctionLaneNavigableNode.getTrafficLoad() < 0.0001) {
                redLightsQueue.remove(junctionLaneNavigableNode);
                redLightsQueue.add(junctionLaneNavigableNode);
            }
        }

        // STEP 5: For all red traffic lights with waiting vehicles, turn green if allowed
        for (JunctionLaneNavigableNode lane : new ArrayList<>(redLightsQueue)) {
            if (lane.getTrafficLoad() > 0.00001 && trafficLightJunction.canLaneTurnGreen(lane)) {
                redLightsQueue.remove(lane);
                lane.setGreenLight(true);
                greenLanesTime.put(lane, TrafficLightConfig.getMinimumTimeGreenLight());
            }
        }
    }

    @Override
    public void initialize() {
        // Get all lanes
        this.redLightsQueue.addAll(this.trafficLightJunction.getLanes());
    }

}

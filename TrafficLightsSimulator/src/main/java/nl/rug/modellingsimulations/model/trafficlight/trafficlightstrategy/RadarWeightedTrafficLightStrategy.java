package nl.rug.modellingsimulations.model.trafficlight.trafficlightstrategy;

import nl.rug.modellingsimulations.config.TrafficLightConfig;
import nl.rug.modellingsimulations.model.navigablenode.JunctionLaneNavigableNode;
import nl.rug.modellingsimulations.model.navigablenode.VehicleBuffer;
import nl.rug.modellingsimulations.model.trafficlight.TrafficLightJunction;

import java.util.*;
import java.util.stream.Collectors;

public class RadarWeightedTrafficLightStrategy implements TrafficLightStrategy {

    private final TrafficLightJunction trafficLightJunction;
    private List<JunctionLaneNavigableNode> redLightsQueue = new ArrayList<>();
    private Map<JunctionLaneNavigableNode, Integer> greenLanesTime = new HashMap<>();

    public RadarWeightedTrafficLightStrategy(TrafficLightJunction trafficLightJunction) {
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

        // STEP 3: For all green lights with a timeout == 0, add to the red queue.
        for (Map.Entry<JunctionLaneNavigableNode, Integer> greenLaneTime : new ArrayList<>(greenLanesTime.entrySet())) {
            if (greenLaneTime.getValue() == 0) {
                greenLanesTime.remove(greenLaneTime.getKey());
                greenLaneTime.getKey().setGreenLight(false);
                redLightsQueue.add(greenLaneTime.getKey());
            }
        }

        // STEP 4: Sort all red lights based on the amount of vehicles that are waiting
        // -- A lane with more vehicles will always take precedence over a more quiet lane
        // NOTE: we are sorting every single time, because we cannot use a comparator since the ordering changes every
        //  iteration.
        redLightsQueue = redLightsQueue.stream()
                .sorted(Comparator.comparingDouble(VehicleBuffer::getTrafficLoad).reversed())
                .collect(Collectors.toList());

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

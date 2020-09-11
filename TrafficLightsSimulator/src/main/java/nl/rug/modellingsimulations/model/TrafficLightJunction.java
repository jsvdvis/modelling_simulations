package nl.rug.modellingsimulations.model;

import nl.rug.modellingsimulations.model.navigablenode.JunctionExitNavigableNode;
import nl.rug.modellingsimulations.model.navigablenode.JunctionLaneNavigableNode;
import nl.rug.modellingsimulations.model.trafficlightstrategy.TrafficLightStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class TrafficLightJunction {

    private Map<JunctionLaneNavigableNode, JunctionExitNavigableNode> lanes;
    private TrafficLightStrategy trafficLightStrategy;

    public TrafficLightJunction(TrafficLightStrategy trafficLightStrategy) {
        this.lanes = new HashMap<>();
        this.trafficLightStrategy = trafficLightStrategy;
    }

    public void setTrafficLightStrategy(TrafficLightStrategy trafficLightStrategy) {
        this.trafficLightStrategy = trafficLightStrategy;
    }

    public TrafficLightStrategy getTrafficLightStrategy() {
        return this.trafficLightStrategy;
    }
}

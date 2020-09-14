package nl.rug.modellingsimulations.model;

import nl.rug.modellingsimulations.model.navigablenode.JunctionExitNavigableNode;
import nl.rug.modellingsimulations.model.navigablenode.JunctionLaneNavigableNode;
import nl.rug.modellingsimulations.model.trafficlightstrategy.TrafficLightStrategy;

import java.util.*;

/**
 *
 */
public class TrafficLightJunction {

    private Map<JunctionLaneNavigableNode, JunctionExitNavigableNode> lanes;
    private Map<JunctionLaneNavigableNode, Set<JunctionLaneNavigableNode>> exemptedLanes = new HashMap<>();
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

    public void addLane(JunctionLaneNavigableNode lane, JunctionExitNavigableNode exit) {
        this.lanes.put(lane, exit);
    }

    public List<JunctionLaneNavigableNode> getJunctionLanes() {
        return new ArrayList<>(lanes.keySet());
    }

    public void addExemptedLane(JunctionLaneNavigableNode master, JunctionLaneNavigableNode slave) {
        if (!this.exemptedLanes.containsKey(master)) {
            this.exemptedLanes.put(master, new HashSet<>());
        }
        Set<JunctionLaneNavigableNode> exemptedLanesForMaster = this.exemptedLanes.get(master);
        if (exemptedLanesForMaster.contains(slave)) {
            throw new IllegalStateException("The specified navigable lane already has this lane on the exempted list.");
        }
        exemptedLanesForMaster.add(slave);
    }
}

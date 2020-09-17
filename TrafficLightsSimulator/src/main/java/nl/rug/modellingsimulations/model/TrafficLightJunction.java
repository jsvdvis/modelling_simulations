package nl.rug.modellingsimulations.model;

import nl.rug.modellingsimulations.model.navigablenode.JunctionExitNavigableNode;
import nl.rug.modellingsimulations.model.navigablenode.JunctionLaneNavigableNode;
import nl.rug.modellingsimulations.model.navigablenode.RoadNavigableNode;
import nl.rug.modellingsimulations.model.trafficlightstrategy.TrafficLightStrategy;
import nl.rug.modellingsimulations.utilities.Point;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public abstract class TrafficLightJunction {

    private final Set<JunctionLaneNavigableNode> lanes;
    private final Map<JunctionLaneNavigableNode, Set<JunctionLaneNavigableNode>> exemptedLanes = new HashMap<>();
    private final TrafficLightStrategy trafficLightStrategy;
    private final Point position;

    public TrafficLightJunction(TrafficLightStrategy trafficLightStrategy, Point position) {
        this.lanes = new HashSet<>();
        this.trafficLightStrategy = trafficLightStrategy;
        this.position = position;
    }

    public TrafficLightStrategy getTrafficLightStrategy() {
        return this.trafficLightStrategy;
    }

    public void addLane(JunctionLaneNavigableNode lane) {
        this.lanes.add(lane);
        lane.setJunction(this);
        lane.getJunctionExitNode().setJunction(this);
    }

    public Set<JunctionLaneNavigableNode> getJunctionLanes() {
        return lanes;
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

    public Point getPosition() {
        return this.position;
    }

    public Collection<List<JunctionLaneNavigableNode>> getJunctionLaneFromSameRoad() {
        return lanes.parallelStream()
                .collect(Collectors.groupingBy(JunctionLaneNavigableNode::getPreviousNodes)).values();
    }

    public List<RoadNavigableNode> getSourceRoads() {
        // TODO: Use getPreviousNodes
        return lanes.parallelStream()
                .map(JunctionLaneNavigableNode::getSourceRoad)
                .collect(Collectors.toList());
    }

    public List<JunctionExitNavigableNode> getJunctionExits() {
        return this.lanes.parallelStream()
                .map(junctionLane -> (List<JunctionExitNavigableNode>)(List<?>) junctionLane.getNextNodes())
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
    }
}

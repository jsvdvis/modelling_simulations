package nl.rug.modellingsimulations.model;

import nl.rug.modellingsimulations.config.JunctionSpacingConfig;
import nl.rug.modellingsimulations.model.navigablenode.JunctionExitNavigableNode;
import nl.rug.modellingsimulations.model.navigablenode.JunctionLaneNavigableNode;
import nl.rug.modellingsimulations.model.navigablenode.NavigableNode;
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

    /**
     * Obtain an ordered list of all nodes on one side of the junction, ordered from left to right
     * @return
     */
    public List<NavigableNode> getJunctionLaneOrExitFromSameRoad(JunctionLaneNavigableNode lane) {
        List<NavigableNode> orderedNodes = new ArrayList<>();

        // Obtain the exit on the same side as this lane, if any
        // This is always 1 exit, and is the first from left to right.
        this.lanes.stream()
                .map(JunctionLaneNavigableNode::getJunctionExitNode)
                .filter(exit -> exit.getNextNodeAfterRoad().equals(lane.getNodeBeforeSourceRoad()))
                .forEach(orderedNodes::add);

        // Add the other nodes on the same side of the current lane
        this.lanes.stream()
                // Obtain the other lanes on the same side
                .filter(sameSidedLane -> sameSidedLane.getSourceRoad().equals(sameSidedLane.getSourceRoad()))
                // Obtain the junctionSourceSink they link to AFTER the junction, and compare the angle and sort on that
                .sorted((lane1, lane2) -> {
                    double angleLane1WithAfterJunction = lane1.getJunctionExitNode()
                            .getNextNodeAfterRoad()
                            .getPosition(false)
                            .getAngle(getPosition());
                    double angleLane2WithAfterJunction = lane2.getJunctionExitNode()
                            .getNextNodeAfterRoad()
                            .getPosition(false)
                            .getAngle(getPosition());
                    return Double.compare(angleLane1WithAfterJunction, angleLane2WithAfterJunction);
                }
                ).forEach(orderedNodes::add);

        return orderedNodes;
    }

    public List<NavigableNode> getJunctionLaneOrExitFromSameRoad(JunctionExitNavigableNode exit) {
        // The exit is always the first one in the order.
        List<NavigableNode> orderedNodes = List.of(exit);

        // TODO

        return orderedNodes;
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

    public Point getPositionOfLanesOrExit(NavigableNode laneOrExit) {
        // First, obtain the coordinate that we want to move our point a little bit towards
        Point otherNodePosition;
        if(laneOrExit instanceof JunctionLaneNavigableNode) {
            // Getting relative position of a lane
            if (((JunctionLaneNavigableNode) laneOrExit).getNodeBeforeSourceRoad() instanceof JunctionExitNavigableNode) {
                // The node before this lane is a junction exit
                // Let's use the coord of the junction, instead of the exit to prevent recursion
                otherNodePosition = ((JunctionExitNavigableNode) ((JunctionLaneNavigableNode) laneOrExit).getNodeBeforeSourceRoad()).getJunction().getPosition();
            } else {
                // It's probably a sink or source. Just take that position
                otherNodePosition = ((JunctionLaneNavigableNode) laneOrExit).getNodeBeforeSourceRoad().getPosition(true);
            }
        } else if(laneOrExit instanceof JunctionExitNavigableNode) {
            // Getting relative position of a junction exit
            if(((JunctionExitNavigableNode) laneOrExit).getNextNodeAfterRoad() instanceof JunctionLaneNavigableNode) {
                // The node after this exit is another junction's lane.
                // Let's use the coord of the junction to prevent recursion
                otherNodePosition = ((JunctionLaneNavigableNode) ((JunctionExitNavigableNode) laneOrExit).getNextNodeAfterRoad()).getJunction().getPosition();
            } else {
                // Not a junction, can safely pick default coord
                otherNodePosition = ((JunctionExitNavigableNode) laneOrExit).getNextNodeAfterRoad().getPosition(true);
            }
        } else {
            throw new IllegalStateException("Trying to get relative position to junction of object not a lane or exit.");
        }

        // Translate junction coordinate from cartesian to polar
        double theta = (getPosition().getAngle(otherNodePosition));
        double r = 0;

        // Add a bit of offset to the lane
        r += JunctionSpacingConfig.getJunctionLaneExitOffset();

        // Go from polar to cartesian
        double x = getPosition().getX() + r * Math.cos(theta);
        double y = getPosition().getY() + r * Math.sin(theta);

        return new Point(x, y);
    }
}

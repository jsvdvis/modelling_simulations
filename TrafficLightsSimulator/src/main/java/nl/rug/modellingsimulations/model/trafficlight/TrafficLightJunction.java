package nl.rug.modellingsimulations.model.trafficlight;

import nl.rug.modellingsimulations.config.JunctionSpacingConfig;
import nl.rug.modellingsimulations.metrics.AveragedTrafficLightJunctionThroughput;
import nl.rug.modellingsimulations.metrics.TrafficLightJunctionThroughput;
import nl.rug.modellingsimulations.model.navigablenode.JunctionExitNavigableNode;
import nl.rug.modellingsimulations.model.navigablenode.JunctionLaneNavigableNode;
import nl.rug.modellingsimulations.model.navigablenode.NavigableNode;
import nl.rug.modellingsimulations.model.navigablenode.RoadNavigableNode;
import nl.rug.modellingsimulations.model.trafficlight.trafficlightstrategy.TrafficLightStrategy;
import nl.rug.modellingsimulations.utilities.Point;
import nl.rug.modellingsimulations.utilities.Vector;

import java.util.*;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 *
 */
public abstract class TrafficLightJunction {

    private final Set<JunctionLaneNavigableNode> lanes;
    private Map<JunctionLaneNavigableNode, Set<JunctionLaneNavigableNode>> exemptedLanes = new HashMap<>();
    private TrafficLightStrategy trafficLightStrategy;
    private final Point position;
    private AveragedTrafficLightJunctionThroughput throughputMeasurer;

    public TrafficLightJunction(Point position) {
        this.lanes = new HashSet<>();
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

    public void removeLane(JunctionLaneNavigableNode lane) {
        this.lanes.remove(lane);
    }

    public void setExemptedLanes(Map<JunctionLaneNavigableNode, Set<JunctionLaneNavigableNode>> exemptedLanes) {
        this.exemptedLanes = exemptedLanes;
    }

    public Point getPosition() {
        return this.position;
    }

    /**
     * Obtain an ordered list of all nodes on one side of the junction, ordered from left to right
     * @return
     */
    public List<NavigableNode> getJunctionLaneOrExitFromSameSide(NavigableNode laneOrExit) {
        List<NavigableNode> orderedNodes = new ArrayList<>();
        Vector previousJunctionAngle;

        // Obtain the exit on the same side as this lane, if any
        // This is always 1 exit, and is the first from left to right.
        if(laneOrExit instanceof JunctionLaneNavigableNode) {
            this.lanes.stream()
                    .map(JunctionLaneNavigableNode::getJunctionExitNode)
                    .filter(exit -> {
                        NavigableNode nodeAfterExit = exit.getNextNodeAfterRoad();
                        NavigableNode nodeBeforeLane = ((JunctionLaneNavigableNode) laneOrExit).getNodeBeforeSourceRoad();
                        if(nodeBeforeLane instanceof JunctionExitNavigableNode) {
                            // Instead of checking if the nodes are equal, we are checking if the junctions are equal.
                            return nodeAfterExit instanceof JunctionLaneNavigableNode &&
                                    ((JunctionExitNavigableNode) nodeBeforeLane).getJunction().equals(((JunctionLaneNavigableNode) nodeAfterExit).getJunction());
                        } else {
                            // The previous node is NOT a junction (so a sink/source), so we simply check if they are equal.
                            return exit.getNextNodeAfterRoad().equals(((JunctionLaneNavigableNode)laneOrExit).getNodeBeforeSourceRoad());
                        }
                    })
                    .forEach(orderedNodes::add);

            previousJunctionAngle = new Vector(
                    getPosition(),
                    ((JunctionLaneNavigableNode) laneOrExit)
                            .getNodeBeforeSourceRoad()
                            .getPosition(false)
            );
        } else if(laneOrExit instanceof JunctionExitNavigableNode) {
            orderedNodes.add(laneOrExit);

            previousJunctionAngle = new Vector(
                    getPosition(),
                    ((JunctionExitNavigableNode) laneOrExit)
                            .getNextNodeAfterRoad()
                            .getPosition(false)
            );
        } else {
            throw new IllegalStateException("Input must be a valid Junction Lane or Junction Exit.");
        }

        // First, obtain the list of lanes on the same side of the junction as the lane/exit
        Predicate<JunctionLaneNavigableNode> isLaneSameSideAsNode;
        if(laneOrExit instanceof JunctionLaneNavigableNode) {
            isLaneSameSideAsNode = potentialLane ->
                    potentialLane.getSourceRoad().equals(
                            ((JunctionLaneNavigableNode) laneOrExit).getSourceRoad()
                    );
        } else {
            isLaneSameSideAsNode = potentialLane -> {
                NavigableNode nodeAfterExit = ((JunctionExitNavigableNode) laneOrExit).getNextNodeAfterRoad();
                NavigableNode nodeBeforeLane = potentialLane.getNodeBeforeSourceRoad();
                if(nodeAfterExit instanceof JunctionLaneNavigableNode) {
                    // Instead of checking if the nodes are equal, we are checking if the junctions are equal.
                    return nodeBeforeLane instanceof JunctionExitNavigableNode &&
                            ((JunctionLaneNavigableNode) nodeAfterExit).getJunction().equals(((JunctionExitNavigableNode) nodeBeforeLane).getJunction());
                } else {
                    // The previous node is NOT a junction (so a sink/source), so we simply check if they are equal.
                    return nodeAfterExit.equals(nodeBeforeLane);
                }
            };
        }

        // Add the other nodes on the same side of the current lane
        this.lanes.stream()
                // Obtain the other lanes on the same side
                .filter(isLaneSameSideAsNode)
                // Obtain the junctionSourceSink they link to AFTER the junction, and compare the angle and sort on that
                .sorted((lane1, lane2) -> {
                    Vector vLane1 = new Vector(
                            getPosition(),
                            lane1.getJunctionExitNode()
                            .getNextNodeAfterRoad()
                            .getPosition(false)
                    );
                    Vector vLane2 = new Vector(
                            getPosition(),
                            lane2.getJunctionExitNode()
                                    .getNextNodeAfterRoad()
                                    .getPosition(false)
                    );

                    return Double.compare(
                            previousJunctionAngle.getRelativeAngle(vLane1),
                            previousJunctionAngle.getRelativeAngle(vLane2)
                    );
                })
                .forEach(orderedNodes::add);

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

    public Point getPositionOfLanesOrExitSideGroup(NavigableNode laneOrExit) {
        Point otherNodePosition = getPositionOfNodeLaneOrExitConnectsTo(laneOrExit);

        return getPosition().moveTowards(otherNodePosition, JunctionSpacingConfig.getJunctionLaneExitOffset());
    }

    public Point getPositionOfNodeLaneOrExitConnectsTo(NavigableNode laneOrExit) {
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
        return otherNodePosition;
    }

    public Point getPositionOfLanesOrExit(NavigableNode junctionLaneOrExit) {
        if(!(junctionLaneOrExit instanceof JunctionLaneNavigableNode) &&
                !(junctionLaneOrExit instanceof JunctionExitNavigableNode))
            throw new IllegalStateException("May only call function on a lane or exit!");

        // First, we obtain the position of the junction with a small offset to the side that we are on.
        Point positionJunctionSide = this.getPositionOfLanesOrExitSideGroup(junctionLaneOrExit);

        // Rotate the position of the other junction by 90 degrees.
        Point otherNodePoint = this.getPositionOfNodeLaneOrExitConnectsTo(junctionLaneOrExit);
        otherNodePoint = otherNodePoint.rotate_around(positionJunctionSide, 90);

        // Next, we get the index of this node in the "list" of lanes/exits on the same side of the road
        List<NavigableNode> nodesOnSide = this.getJunctionLaneOrExitFromSameSide(junctionLaneOrExit);
        int nthNodeOnSideOfJunction = nodesOnSide.indexOf(junctionLaneOrExit);
        double centerIndex = (nodesOnSide.size()-1) / 2.0;
        double differenceFromCenterIndex = (nthNodeOnSideOfJunction - centerIndex);
        double distanceToMoveToSide = JunctionSpacingConfig.getSameSideNodeOffset() * differenceFromCenterIndex;

        return positionJunctionSide.moveTowards(otherNodePoint, distanceToMoveToSide);
    }

    public void setTrafficLightStrategy(TrafficLightStrategy trafficLightStrategy) {
        this.trafficLightStrategy = trafficLightStrategy;
    }

    public void initializeStrategy() {
        this.trafficLightStrategy.initialize();
    }

    public List<List<JunctionLaneNavigableNode>> getLanesSameJunctionSideGroups() {
        return this.lanes.stream()
                .map(this::getJunctionLaneOrExitFromSameSide)
                .map(listLaneExit ->
                        listLaneExit
                                .stream()
                                .filter(laneOrExit -> laneOrExit instanceof JunctionLaneNavigableNode)
                                .map(x -> (JunctionLaneNavigableNode) x)
                                .collect(Collectors.toList())
                )
                .distinct()
                .collect(Collectors.toList());
    }

    public void setAllTrafficLightsToRed() {
        this.lanes
                .forEach(lane -> lane.setGreenLight(false));
    }

    public Set<JunctionLaneNavigableNode> getLanes() {
        return lanes;
    }

    public boolean canLaneTurnGreen(JunctionLaneNavigableNode junctionLaneNavigableNode) {
        return exemptedLanes.get(junctionLaneNavigableNode).parallelStream()
                .noneMatch(JunctionLaneNavigableNode::isGreenLight)
                && (junctionLaneNavigableNode.getJunctionExitNode().getTrafficLoad() <= 0.999 ||
                junctionLaneNavigableNode.getJunctionExitNode().getNextNodeAfterRoad().getTrafficLoad() <= 0.999);
    }

    public void setThroughputMeasurer(AveragedTrafficLightJunctionThroughput throughputMeasurer) {
        this.throughputMeasurer = throughputMeasurer;
    }

    public void registerVehicleMovingThroughJunction() {
        if (this.throughputMeasurer != null) {
            this.throughputMeasurer.increaseThroughput();
        }
    }
}

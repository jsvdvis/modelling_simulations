package nl.rug.modellingsimulations.simulation;

import nl.rug.modellingsimulations.model.trafficlight.TrafficLightJunction;
import nl.rug.modellingsimulations.model.navigablenode.*;
import nl.rug.modellingsimulations.model.trafficlightstrategy.TrafficLightStrategy;
import nl.rug.modellingsimulations.utilities.Point;
import nl.rug.modellingsimulations.utilities.RandomGenerator;
import nl.rug.modellingsimulations.utilities.SortByJunctionDirection;

import java.awt.geom.Line2D;
import java.util.*;
import java.util.stream.Collectors;

public class SimulationBuilder {

    private final int MAX_CONNECTIONS = 4;
    private final Set<TrafficLightJunction> junctions = new HashSet<>();
    private final Set<NavigableNode> nodes = new HashSet<>();
    private final Map<TrafficLightJunction, Set<TrafficLightJunction>> connections = new HashMap<>();
    private final Map<TrafficLightJunction, VehicleSourceNavigableNode> sources = new HashMap<>();
    private final Map<TrafficLightJunction, VehicleSinkNavigableNode> sinks = new HashMap<>();
    private final Simulation simulation;

    public SimulationBuilder(Simulation simulation) {
        this.simulation = simulation;
    }

    private void addJunction(TrafficLightJunction junction) {
        if (!this.connections.containsKey(junction)) {
            this.connections.put(junction, new HashSet<>());
        }
        junctions.add(junction);
    }

    public void connectTwoWayJunction(TrafficLightJunction first, TrafficLightJunction second) {
        this.connect(first, second);
        this.connect(second, first);
    }

    public void connect(TrafficLightJunction from, TrafficLightJunction to) {
        this.addJunction(from);
        this.addJunction(to);

        Set<TrafficLightJunction> currentConnections = this.connections.get(from);
        if (currentConnections.contains(to)) {
            throw new IllegalStateException("This connection already exists.");
        }
        if (currentConnections.size() >= MAX_CONNECTIONS) {
            throw new IllegalStateException("This node already has the maximum number of connections");
        }
        currentConnections.add(to);
    }

    public void connect(TrafficLightJunction from, VehicleSinkNavigableNode to) {
        this.sinks.put(from, to);
        nodes.add(to);
    }

    public void connect(VehicleSourceNavigableNode from, TrafficLightJunction to) {
        this.sources.put(to, from);
        nodes.add(from);
    }

    public List<TrafficLightJunction> getJunctions() {
        return new ArrayList<>(junctions);
    }

    public List<NavigableNode> getNavigableNodes() {
        return new ArrayList<>(nodes);
    }

    public void build() {
        // Step 1: Build all roads with their starting- and ending points.
        Map<TrafficLightJunction, Set<RoadNavigableNode>> roadEnds = new HashMap<>();
        Map<TrafficLightJunction, Set<JunctionExitNavigableNode>> roadStarts = new HashMap<>();

        this.connections.entrySet().stream().forEach(entry -> {
            TrafficLightJunction from = entry.getKey();
            Set<JunctionExitNavigableNode> startingRoads = new HashSet<>();
            entry
                    .getValue()
                    .stream()
                    .sorted(new SortByJunctionDirection(from))
                    .forEachOrdered(to -> {
                        RoadNavigableNode road = new RoadNavigableNode(
                                (int)Math.ceil(from.getPosition().getDistance(to.getPosition()))
                        );
                        nodes.add(road);
                        if (!roadEnds.containsKey(to)) {
                            roadEnds.put(to, new HashSet<>());
                        }
                        roadEnds.get(to).add(road);

                        JunctionExitNavigableNode start = new JunctionExitNavigableNode();
                        nodes.add(start);
                        start.addNextNode(road);
                        startingRoads.add(start);
                    });
            roadStarts.put(from, startingRoads);
        });

        // Handle sources.
        this.sources.entrySet().stream().forEach(entry -> {
            VehicleSourceNavigableNode from = entry.getValue();
            TrafficLightJunction to = entry.getKey();

            RoadNavigableNode road = new RoadNavigableNode(1);
            nodes.add(road);
            from.addNextNode(road);
            roadEnds.get(to).add(road);
        });

        // Handle sinks.
        this.sinks.entrySet().stream().forEach(entry -> {
            TrafficLightJunction from = entry.getKey();
            VehicleSinkNavigableNode to = entry.getValue();

            RoadNavigableNode road = new RoadNavigableNode(1);
            nodes.add(road);
            road.addNextNode(to);
            JunctionExitNavigableNode junctionExit = new JunctionExitNavigableNode();
            junctionExit.addNextNode(road);
            nodes.add(junctionExit);
            roadStarts.get(from).add(junctionExit);
        });

        // Step 2: Connect all starting and ending points.
        this.junctions
                .stream()
                .forEach(junction -> {
                    roadEnds
                            .get(junction)
                            .stream()
                            .forEach(road -> {
                                roadStarts
                                        .get(junction)
                                        .stream()
                                        .forEach(junctionExit -> {
                                            JunctionLaneNavigableNode lane = new JunctionLaneNavigableNode(
                                                    simulation.getConfig().getRandomLaneSize()
                                            );
                                            nodes.add(lane);
                                            road.addNextNode(lane);
                                            lane.addNextNode(junctionExit);
                                            junction.addLane(lane);
                                        });
                            });
                });

        // Step 3: Remove turn-backs (caused by two traffic junctions that are connected both ways)
        this.connections.keySet().stream().forEach(from -> {
            this.connections
                    .get(from)
                    .stream()
                    .filter(to -> this.connections.get(to).contains(from))
                    .forEach(to -> {
                        // We now loop over all traffic junctions that are two-way connected, each from-to pair is two
                        // way connected.

                        // First find all roads that go from 'to' to 'from'
                        Set<RoadNavigableNode> roadsFromToToFrom = roadEnds
                                .get(from)
                                .stream()
                                .filter(road -> {
                                    NavigableNode roadStartNode = road.getPreviousNodes().iterator().next();
                                    if (!(roadStartNode instanceof JunctionExitNavigableNode)) {
                                        return false;
                                    }
                                    JunctionExitNavigableNode junctionExit = (JunctionExitNavigableNode) roadStartNode;
                                    return roadStarts.get(to).contains(junctionExit);
                                })
                                .collect(Collectors.toSet());

                        // Then all junction exits that go from 'from' to 'to'
                        Set<JunctionExitNavigableNode> exitsFromFromToTo = roadStarts
                                .get(from)
                                .stream()
                                .filter(junctionExit -> {
                                    NavigableNode roadNode = junctionExit.getNextNodes().get(0);
                                    if (!(roadNode instanceof RoadNavigableNode)) {
                                        return false;
                                    }
                                    RoadNavigableNode road = (RoadNavigableNode) roadNode;
                                    return roadEnds.get(to).contains(road);
                                })
                                .collect(Collectors.toSet());

                        // Now we want to find the lanes that connect the roads to exits and remove each one of them.
                        roadsFromToToFrom.stream().forEach(road -> {
                            Set<JunctionLaneNavigableNode> lanesToRemove = road
                                    .getNextNodes()
                                    .stream()
                                    .filter(lane -> {
                                        JunctionExitNavigableNode junctionExit = (JunctionExitNavigableNode) lane.getNextNodes().get(0);
                                        return exitsFromFromToTo.contains(junctionExit);
                                    })
                                    .map(lane -> (JunctionLaneNavigableNode) lane)
                                    .collect(Collectors.toSet());

                            lanesToRemove.forEach(lane -> {
                                nodes.remove(lane);
                                from.removeLane(lane);
                                for (Iterator<NavigableNode> iterator = lane.getNextNodes().iterator(); iterator.hasNext();) {
                                    lane.removeNextNode(iterator.next());
                                }
                                for (Iterator<NavigableNode> iterator = lane.getPreviousNodes().iterator(); iterator.hasNext();) {
                                    iterator.next().removeNextNode(lane);
                                }
                            });
                        });
                    });
        });
    }

    public void buildExemptedLanes() {
        junctions.stream().forEach(junction -> {
            Map<JunctionLaneNavigableNode, Set<JunctionLaneNavigableNode>> exemptedLanes = new HashMap<>();
            Set<JunctionLaneNavigableNode> junctionLanes = new HashSet<>();

            // Step 1: for each exit, only one lane is allowed to be open at a time.
            junction.getJunctionExits().stream().forEach(junctionExit -> {
                Set<JunctionLaneNavigableNode> lanes = junctionExit
                        .getPreviousNodes()
                        .stream()
                        .map(node -> (JunctionLaneNavigableNode) node)
                        .collect(Collectors.toSet());

                // We need the lanes for the next step. Since we have them now, we might as well add them to a set already.
                junctionLanes.addAll(lanes);

                lanes.stream().forEach(lane -> {
                    if (!exemptedLanes.containsKey(lane)) {
                        exemptedLanes.put(lane, new HashSet<>());
                    }
                    Set<JunctionLaneNavigableNode> otherLanes = exemptedLanes.get(lane);
                    otherLanes.addAll(
                            lanes.stream().filter(otherLane -> !otherLane.equals(lane)).collect(Collectors.toSet())
                    );
                });
            });

            // Step 2: Lane crossings are not allowed.

            // First build lines from each lane to its exit
            Map<JunctionLaneNavigableNode, Line2D> laneLines = new HashMap<>();
            junctionLanes.stream().forEach(lane -> {
                Point lanePosition = lane.getPosition(true);
                Point exitPosition = lane.getJunctionExitNode().getPosition(true);
                laneLines.put(lane, new Line2D.Double(
                        lanePosition.getX(),
                        lanePosition.getY(),
                        exitPosition.getX(),
                        exitPosition.getY()
                ));
            });

            // Then add the lanes whose line intersect with the current to the set.
            junctionLanes.stream().forEach(lane -> {
                Line2D laneLine = laneLines.get(lane);
                Set<JunctionLaneNavigableNode> intersectingLanes = junctionLanes
                        .stream()
                        // Exclude itself
                        .filter(otherLane -> !lane.equals(otherLane))
                        // Exempted lanes don't need to be checked again.
                        .filter(otherLane -> !exemptedLanes.get(lane).contains(otherLane))
                        // Filter on intersection with other lanes
                        .filter(otherLane -> laneLine.intersectsLine(laneLines.get(otherLane)))
                        .collect(Collectors.toSet());

                exemptedLanes.get(lane).addAll(intersectingLanes);
            });

            junction.setExemptedLanes(exemptedLanes);
        });
    }
}

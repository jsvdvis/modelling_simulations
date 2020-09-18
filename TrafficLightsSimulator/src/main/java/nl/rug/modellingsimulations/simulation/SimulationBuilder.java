package nl.rug.modellingsimulations.simulation;

import nl.rug.modellingsimulations.model.TrafficLightJunction;
import nl.rug.modellingsimulations.model.navigablenode.*;
import nl.rug.modellingsimulations.utilities.RandomGenerator;
import nl.rug.modellingsimulations.utilities.SortByJunctionDirection;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class SimulationBuilder {

    private final int MAX_CONNECTIONS = 4;
    private final Set<TrafficLightJunction> junctions = new HashSet<>();
    private final Set<NavigableNode> nodes = new HashSet<>();
    private final Map<TrafficLightJunction, Set<TrafficLightJunction>> connections = new HashMap<>();
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

//        RoadNavigableNode newRoad = new RoadNavigableNode(
//            (int)Math.ceil(from.getPosition().getDistance(to.getPosition()))
//        );
//        nodes.add(newRoad);
//        JunctionExitNavigableNode laneExit = new JunctionExitNavigableNode();
//        nodes.add(laneExit);
//        laneExit.addNextNode(newRoad);
//
//        // Create junction lanes for all roads ending at from.
//        from
//                .getSourceRoads()
//                .stream()
//                .forEach(fromRoad -> {
//                    JunctionLaneNavigableNode laneStart = new JunctionLaneNavigableNode(
//                            simulation.getConfig().getRandomLaneSize()
//                    );
//                    nodes.add(laneStart);
//                    from.addLane(laneStart);
//                    fromRoad.addNextNode(laneStart);
//                    laneStart.addNextNode(laneExit);
//                });
//
//        // Create junction lanes for all roads starting at to.
//        to
//                .getJunctionExits()
//                .stream()
//                .forEach(toLaneExit -> {
//                    JunctionLaneNavigableNode laneStart = new JunctionLaneNavigableNode(
//                            simulation.getConfig().getRandomLaneSize()
//                    );
//                    nodes.add(laneStart);
//                    from.addLane(laneStart);
//                    newRoad.addNextNode(laneStart);
//                    laneStart.addNextNode(toLaneExit);
//                });


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
        RoadNavigableNode road = new RoadNavigableNode(1);
        nodes.add(road);
        road.addNextNode(to);

        // Create new JunctionExit
        JunctionExitNavigableNode junctionExitNavigableNode = new JunctionExitNavigableNode();
        nodes.add(junctionExitNavigableNode);
        junctionExitNavigableNode.addNextNode(road);

        // For each road going into a junction, create a new lane that connects with the sink
        from.getSourceRoads().forEach(incomingRoad -> {
            // Create new JunctionLane
            JunctionLaneNavigableNode junctionLaneNavigableNode = new JunctionLaneNavigableNode(1);
            nodes.add(junctionLaneNavigableNode);
            incomingRoad.addNextNode(junctionLaneNavigableNode);

            // Connect lanes and junctions
            junctionLaneNavigableNode.addNextNode(junctionExitNavigableNode);
            from.addLane(junctionLaneNavigableNode);
        });
    }

    public void connect(VehicleSourceNavigableNode from, TrafficLightJunction to) {
        // The Source connects to a road, leading up to lanes in a junction.
        RoadNavigableNode road = new RoadNavigableNode(1);
        nodes.add(road);
        from.addNextNode(road);
        nodes.add(from);

        // For each Junction exit, create a lane to that junction and connect them
        to.getJunctionExits().forEach(exit -> {
                    JunctionLaneNavigableNode lane = new JunctionLaneNavigableNode(1);
                    road.addNextNode(lane);
                    nodes.add(lane);
                    lane.addNextNode(exit);
                    to.addLane(lane);
        });
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
    }
}

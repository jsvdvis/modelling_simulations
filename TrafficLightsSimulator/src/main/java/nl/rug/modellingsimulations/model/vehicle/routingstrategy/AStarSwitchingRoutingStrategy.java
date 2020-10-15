package nl.rug.modellingsimulations.model.vehicle.routingstrategy;

import nl.rug.modellingsimulations.config.VehicleRoutingStrategyConfig;
import nl.rug.modellingsimulations.model.navigablenode.JunctionLaneNavigableNode;
import nl.rug.modellingsimulations.model.navigablenode.NavigableNode;
import nl.rug.modellingsimulations.model.navigablenode.RoadNavigableNode;
import nl.rug.modellingsimulations.model.navigablenode.VehicleSinkNavigableNode;
import nl.rug.modellingsimulations.model.vehicle.Vehicle;
import nl.rug.modellingsimulations.utilities.AllNodesUtility;
import nl.rug.modellingsimulations.utilities.RandomGenerator;

import java.util.*;
import java.util.stream.Collectors;

public class AStarSwitchingRoutingStrategy implements RoutingStrategy {

    private final Vehicle vehicle;
    Map<NavigableNode, Double> scoreFromStart;
    Map<NavigableNode, Double> estimatedScoreToGoal;
    Map<NavigableNode, NavigableNode> cameFrom;
    PriorityQueue<NavigableNode> openSet;
    Map<NavigableNode, NavigableNode> nodeToPick = new HashMap<>();
    int impatienceTurnsWaiting = 0;
    NavigableNode goal = null;

    public AStarSwitchingRoutingStrategy(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    @Override
    public NavigableNode getNextNode() {
        if(!nodeToPick.containsKey(vehicle.getCurrentNavigableNode())) {
            initializeAStar();
        }

        return this.nodeToPick.get(vehicle.getCurrentNavigableNode());
    }

    public void updateImpatience() {
        NavigableNode currentNode = vehicle.getCurrentNavigableNode();
        NavigableNode nextNode = getNextNode();

        // If the next node is full, impatience starts to build up!
        if(currentNode instanceof RoadNavigableNode &&
                nextNode.getTrafficLoad() >= 0.999 &&
                nextNode instanceof JunctionLaneNavigableNode
        ) {
            impatienceTurnsWaiting++;

            // If the vehicle has been waiting for a long time and impatience is high enough,
            // switch to a random different lane only if there is a non-full lane available
            if(RandomGenerator.getInstance().getIntegerBetween(0, VehicleRoutingStrategyConfig.getTurnsFullImpatience()) <=
                    VehicleRoutingStrategyConfig.getTurnsFullImpatience()) {
                // the vehicle now got impatient and tries to switch lanes if possible
                List<JunctionLaneNavigableNode> options = vehicle.getCurrentNavigableNode().getNextNodes().parallelStream()
                        .map(lane -> (JunctionLaneNavigableNode) lane)
                        .filter(lane -> lane.getTrafficLoad() < 0.999)
                        //.filter(lane -> !(lane.getJunctionExitNode().getNextNodeAfterRoad() instanceof VehicleSinkNavigableNode))
                        .collect(Collectors.toList());

                if(options.size() > 0) {
                    nextNode = RandomGenerator.getInstance().getRandomOfList(options);
                    impatienceTurnsWaiting = 0;
                    nodeToPick.put(vehicle.getCurrentNavigableNode(), nextNode);
                }
            }
        } else {
            impatienceTurnsWaiting = 0;
        }
    }

    private void initializeAStar() {
        NavigableNode source = vehicle.getCurrentNavigableNode();
        if(this.goal == null)
            goal = RandomGenerator.getInstance().getRandomOfList(AllNodesUtility.getInstance().getAllSinks());

        openSet = new PriorityQueue<>(Comparator.comparingDouble(node -> estimatedScoreToGoal.get(node)));
        scoreFromStart = new HashMap<>();
        estimatedScoreToGoal = new HashMap<>();
        cameFrom = new HashMap<>();
        nodeToPick.clear();
        impatienceTurnsWaiting = 0;

        scoreFromStart.put(source, 0.0);
        estimatedScoreToGoal.put(source, scoreFromStart.get(source) + h(source, goal));
        openSet.add(source);

        while(openSet.size() > 0) {
            NavigableNode current = openSet.poll();
            if(current.equals(goal)) {
                buildPath(current);
                return;
            }

            for(NavigableNode neighbour : current.getNextNodes()) {
                double startToNeighbour = scoreFromStart.get(current) + d(current, neighbour);
                if(scoreFromStart.get(neighbour) == null || startToNeighbour < scoreFromStart.get(neighbour)) {
                    cameFrom.put(neighbour, current);
                    scoreFromStart.put(neighbour, startToNeighbour);
                    estimatedScoreToGoal.put(neighbour, scoreFromStart.get(neighbour) + h(neighbour, goal));
                    if(!openSet.contains(neighbour))
                        openSet.add(neighbour);
                }
            }
        }
        throw new IllegalStateException("No nodes left to process, but never reached the goal!");
    }

    private void buildPath(NavigableNode current) {
        while(cameFrom.containsKey(current)) {
            nodeToPick.put(cameFrom.get(current), current);
            current = cameFrom.get(current);
        }
    }

    private double h(NavigableNode start, NavigableNode goal) {
        return start.getPosition(false).getDistance(goal.getPosition(false));
    }

    private double d(NavigableNode from, NavigableNode to) {
        //return from.getPosition(false).getDistance(to.getPosition(false));
        return 1.0;
    }

}

package nl.rug.modellingsimulations.model.vehicle.routingstrategy;

import nl.rug.modellingsimulations.model.navigablenode.NavigableNode;
import nl.rug.modellingsimulations.model.navigablenode.VehicleSinkNavigableNode;
import nl.rug.modellingsimulations.model.vehicle.Vehicle;
import nl.rug.modellingsimulations.utilities.RandomGenerator;

import java.util.*;
import java.util.stream.Collectors;

public class AStarPatientRoutingStrategy implements RoutingStrategy {

    private final Vehicle vehicle;
    Map<NavigableNode, Double> scoreFromStart;
    Map<NavigableNode, Double> estimatedScoreToGoal;
    Map<NavigableNode, NavigableNode> cameFrom;
    PriorityQueue<NavigableNode> openSet;
    Map<NavigableNode, NavigableNode> nodeToPick = new HashMap<>();


    public AStarPatientRoutingStrategy(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    @Override
    public NavigableNode pickNextNode() {
        if(nodeToPick.size() == 0) {
            initializeAStar();
        }

        if(!nodeToPick.containsKey(vehicle.getCurrentNavigableNode()))
            throw new IllegalStateException("Cannot find next node to go to using AStar, since current node not known!");

        return nodeToPick.get(vehicle.getCurrentNavigableNode());
    }

    private List<NavigableNode> getAllNodes() {
        Queue<NavigableNode> toProcess = new LinkedList<>();
        List<NavigableNode> allNodes = new ArrayList<>();
        toProcess.add(vehicle.getCurrentNavigableNode());
        while(toProcess.size() > 0) {
            NavigableNode current = toProcess.poll();
            allNodes.add(current);
            current.getNextNodes().stream()
                    .filter(node -> !allNodes.contains(node))
                    .forEach(toProcess::add);
        }
        return allNodes.stream()
                .filter(node -> node instanceof VehicleSinkNavigableNode)
                .collect(Collectors.toList());
    }

    private void initializeAStar() {
        NavigableNode goal = RandomGenerator.getInstance().getRandomOfList(getAllNodes());
        NavigableNode source = vehicle.getCurrentNavigableNode();

        openSet = new PriorityQueue<>(Comparator.comparingDouble(node -> estimatedScoreToGoal.get(node)));
        scoreFromStart = new HashMap<>();
        estimatedScoreToGoal = new HashMap<>();
        cameFrom = new HashMap<>();

        scoreFromStart.put(source, 0.0);
        estimatedScoreToGoal.put(source, scoreFromStart.get(source) + h(source, goal));
        openSet.add(source);

        while(openSet.size() > 0) {
            NavigableNode current = openSet.poll();
            if(current == goal) {
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

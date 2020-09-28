package nl.rug.modellingsimulations.model.navigablenode;

import nl.rug.modellingsimulations.utilities.Point;

import java.util.*;
import java.util.stream.Collectors;

public class RoadNavigableNode extends VehicleBuffer {

    private List<NavigableNode> toNavigableNodes = new ArrayList<>();
    private Set<NavigableNode> fromNavigableNodes = new HashSet<>();

    public RoadNavigableNode(int size) {
        super(size);
    }

    @Override
    public void addNextNode(NavigableNode next) {
        this.toNavigableNodes.add(next);
        next.addPreviousNode(this);
    }

    @Override
    public void removeNextNode(NavigableNode next) {
        this.toNavigableNodes.remove(next);
        next.removePreviousNode(this);
    }

    @Override
    public Set<NavigableNode> getPreviousNodes() {
        return fromNavigableNodes;
    }

    @Override
    public void addPreviousNode(NavigableNode previous) {
        this.fromNavigableNodes.add(previous);
    }

    @Override
    public void removePreviousNode(NavigableNode previous) {
        this.fromNavigableNodes.remove(previous);
    }

    @Override
    public List<NavigableNode> getNextNodes() {
        return toNavigableNodes;
    }

    @Override
    public Point getPosition(boolean precise) {
        // Halfway between previous node and the next node
        List<Point> nextPoints = toNavigableNodes.stream()
                .map(toNode -> toNode.getPosition(true))
                .collect(Collectors.toList());
        List<Point> prevPoints = fromNavigableNodes.stream()
                .map(fromNode -> fromNode.getPosition(true))
                .collect(Collectors.toList());
        Point avgNextNodes = Point.avgPoint(nextPoints);
        Point avgPrevNodes = Point.avgPoint(prevPoints);
        return avgNextNodes.getHalfWay(avgPrevNodes);
    }

}

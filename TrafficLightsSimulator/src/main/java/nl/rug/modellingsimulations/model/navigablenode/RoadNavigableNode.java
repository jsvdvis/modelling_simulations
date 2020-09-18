package nl.rug.modellingsimulations.model.navigablenode;

import nl.rug.modellingsimulations.model.VehicleBuffer;
import nl.rug.modellingsimulations.model.vehicle.Vehicle;
import nl.rug.modellingsimulations.utilities.Point;

import java.util.*;
import java.util.stream.Collectors;

public class RoadNavigableNode extends VehicleBuffer {

    private List<NavigableNode> toNavigableNodes = new ArrayList<>();
    private Set<NavigableNode> fromNavigableNodes = new HashSet<>();
    private NavigableNode previousNode;

    public RoadNavigableNode(int size) {
        super(size);
    }

    public void addNextNode(NavigableNode next) {
        this.toNavigableNodes.add(next);
        next.addPreviousNode(this);

        if(next instanceof JunctionLaneNavigableNode)
            ((JunctionLaneNavigableNode) next).setSourceRoad(this);
    }

    @Override
    public Set<NavigableNode> getPreviousNodes() {
        return fromNavigableNodes;
    }

    @Override
    public void addPreviousNode(NavigableNode previous) {
        this.fromNavigableNodes.add(previousNode);
    }

    @Override
    public List<NavigableNode> getNextNodes() {
        return toNavigableNodes;
    }

    @Override
    public Point getPosition() {
        // Halfway between previous node and the next node
        List<Point> nextPoints = toNavigableNodes.stream().map(NavigableNode::getPosition).collect(Collectors.toList());
        return Point.avgPoint(nextPoints);
    }

    public void setPreviousNode(NavigableNode previousNode) {
        this.previousNode = previousNode;
    }
}

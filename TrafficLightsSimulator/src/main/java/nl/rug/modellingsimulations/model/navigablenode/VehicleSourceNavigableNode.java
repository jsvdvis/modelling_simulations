package nl.rug.modellingsimulations.model.navigablenode;


import nl.rug.modellingsimulations.utilities.Point;

import java.util.List;
import java.util.Set;

public class VehicleSourceNavigableNode extends VehicleBuffer {

    private NavigableNode navigableNode;
    private Point position;

    public VehicleSourceNavigableNode(Point point) {
        super(1);
        this.position = point;
    }

    @Override
    public List<NavigableNode> getNextNodes() {
        return List.of(navigableNode);
    }

    @Override
    public Point getPosition(boolean precise) {
        return position;
    }

    public void addNextNode(NavigableNode navigableNode) {
        this.navigableNode = navigableNode;
        this.navigableNode.addPreviousNode(this);
    }

    @Override
    public void removeNextNode(NavigableNode next) {
        this.navigableNode = null;
        next.removePreviousNode(this);
    }

    @Override
    public Set<NavigableNode> getPreviousNodes() {
        return Set.of();
    }

    @Override
    public void addPreviousNode(NavigableNode previous) {
        throw new IllegalStateException("Trying to add a previous node to a source.");
    }

    @Override
    public void removePreviousNode(NavigableNode previous) {
        throw new IllegalStateException("Trying to remove a previous node from a source.");
    }

}
package nl.rug.modellingsimulations.model.navigablenode;


import nl.rug.modellingsimulations.model.VehicleBuffer;
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
    public Point getPosition() {
        return position;
    }

    public void addNextNode(NavigableNode navigableNode) {
        this.navigableNode = navigableNode;
        this.navigableNode.addPreviousNode(this);
    }

    @Override
    public Set<NavigableNode> getPreviousNodes() {
        return Set.of();
    }

    @Override
    public void addPreviousNode(NavigableNode previous) {
        throw new IllegalStateException("Trying to add a previous node to a source.");
    }

}
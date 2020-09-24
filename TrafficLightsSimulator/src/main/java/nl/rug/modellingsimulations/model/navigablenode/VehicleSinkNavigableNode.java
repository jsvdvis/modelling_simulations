package nl.rug.modellingsimulations.model.navigablenode;

import nl.rug.modellingsimulations.model.VehicleBuffer;
import nl.rug.modellingsimulations.model.vehicle.Vehicle;
import nl.rug.modellingsimulations.utilities.Point;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class VehicleSinkNavigableNode extends VehicleBuffer {

    private Point point;
    private Set<NavigableNode> previousNodes = new HashSet<>();

    public VehicleSinkNavigableNode(Point point) {
        super(1);
        this.point = point;
    }

    @Override
    public void addNextNode(NavigableNode next) {
        throw new IllegalStateException("Can not add a next node to a sink.");
    }

    @Override
    public void removeNextNode(NavigableNode next) {
        throw new IllegalStateException("Can not remove a next node from a sink.");
    }

    @Override
    public Set<NavigableNode> getPreviousNodes() {
        return previousNodes;
    }

    @Override
    public void addPreviousNode(NavigableNode previous) {
        this.previousNodes.add(previous);
    }

    @Override
    public void removePreviousNode(NavigableNode previous) {
        this.previousNodes.remove(previous);
    }

    @Override
    public List<NavigableNode> getNextNodes() {
        return new ArrayList<>();
//        throw new IllegalStateException("Tried to get next node of a SinkNode.");
    }

    @Override
    public boolean canMovePosition(Vehicle vehicle) {
        return true;
    }

    @Override
    public Point getPosition(boolean precise) {
        return point;
    }

}

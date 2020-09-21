package nl.rug.modellingsimulations.model.navigablenode;

import nl.rug.modellingsimulations.model.TrafficLightJunction;
import nl.rug.modellingsimulations.model.VehicleBuffer;
import nl.rug.modellingsimulations.utilities.Point;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A junction exit is a node that can be visited by vehicles.
 * An exit always points towards a road.
 */
public class JunctionExitNavigableNode extends VehicleBuffer {

    private RoadNavigableNode roadNavigableNode;
    private TrafficLightJunction junction;
    private Set<NavigableNode> previousNodes = new HashSet<>();

    public JunctionExitNavigableNode() {
        super(1);
    }

    @Override
    public void addNextNode(NavigableNode next) {
        if(!(next instanceof RoadNavigableNode))
            throw new IllegalStateException("Junction Exit may only point to a Road.");

        this.roadNavigableNode = (RoadNavigableNode) next;
        next.addPreviousNode(this);
    }

    @Override
    public void removeNextNode(NavigableNode next) {
        this.roadNavigableNode = null;
        next.removePreviousNode(this);
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
        return List.of(roadNavigableNode);
    }

    @Override
    public Point getPosition(boolean precise) {
        if(!precise)
            return junction.getPosition();

        return this.getJunction().getPositionOfLanesOrExit(this);
    }

    public void setJunction(TrafficLightJunction junction) {
        this.junction = junction;
    }

    public TrafficLightJunction getJunction() {
        return junction;
    }

    public NavigableNode getNextNodeAfterRoad() {
        return this.roadNavigableNode.getNextNodes().get(0);
    }
}

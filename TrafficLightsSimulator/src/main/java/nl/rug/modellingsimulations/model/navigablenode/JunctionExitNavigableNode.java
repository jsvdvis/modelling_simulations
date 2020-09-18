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
    public Set<NavigableNode> getPreviousNodes() {
        return previousNodes;
    }

    @Override
    public void addPreviousNode(NavigableNode previous) {
        this.previousNodes.add(previous);
    }

    @Override
    public List<NavigableNode> getNextNodes() {
        return List.of(roadNavigableNode);
    }

    @Override
    public Point getPosition(boolean precise) {
        if(!precise)
            return junction.getPosition();

        // First, we obtain the position of the junction with a small offset to the side that we are on.
        Point positionJunctionSide = this.getJunction().getPositionOfLanesOrExit(this);

        // Next, we need to make a small offset between all nodes on the same side, orthogonal to the prev angle.
        // TODO
        List<NavigableNode> nodesOnSide = this.junction.getJunctionLaneOrExitFromSameRoad(this);
        int amountOfNodesOnSide = nodesOnSide.size();

        return positionJunctionSide;
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

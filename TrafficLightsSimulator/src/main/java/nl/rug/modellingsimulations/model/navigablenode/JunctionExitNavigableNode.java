package nl.rug.modellingsimulations.model.navigablenode;

import nl.rug.modellingsimulations.model.VehicleBuffer;

import java.util.List;

/**
 * A junction exit is a node that can be visited by vehicles.
 * An exit always points towards a road.
 */
public class JunctionExitNavigableNode extends VehicleBuffer {

    private RoadNavigableNode roadNavigableNode;

    public JunctionExitNavigableNode() {
        super(1);
    }

    @Override
    public void addNextNode(NavigableNode next) {
        if(!(next instanceof RoadNavigableNode))
            throw new IllegalStateException("Junction Exit may only point to a Road.");

        this.roadNavigableNode = (RoadNavigableNode) next;
    }

    @Override
    public List<NavigableNode> getNextNodes() {
        return List.of(roadNavigableNode);
    }

}

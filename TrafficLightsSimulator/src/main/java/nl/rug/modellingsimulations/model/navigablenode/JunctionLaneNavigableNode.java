package nl.rug.modellingsimulations.model.navigablenode;

import nl.rug.modellingsimulations.model.VehicleBuffer;
import nl.rug.modellingsimulations.model.vehicle.Vehicle;

import java.util.List;

/**
 * Each junctionlane is a small queue in front of a traffic light.
 * Here, vehicles will wait until the light goes green, before moving to the next node.
 */
public class JunctionLaneNavigableNode extends VehicleBuffer {

    private JunctionExitNavigableNode junctionExitNode;

    public JunctionLaneNavigableNode(int size) {
        super(size);
    }

    @Override
    public List<NavigableNode> getNextNodes() {
        return List.of(junctionExitNode);
    }

}

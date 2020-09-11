package nl.rug.modellingsimulations.model.navigablenode;

import nl.rug.modellingsimulations.model.VehicleBuffer;
import nl.rug.modellingsimulations.model.vehicle.Vehicle;

import java.util.List;

public class RoadNavigableNode extends VehicleBuffer {

    private List<NavigableNode> toNavigableNode;

    public RoadNavigableNode(int size) {
        super(size);
    }

    @Override
    public List<NavigableNode> getNextNodes() {
        return toNavigableNode;
    }

}

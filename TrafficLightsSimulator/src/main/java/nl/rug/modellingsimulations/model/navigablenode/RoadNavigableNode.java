package nl.rug.modellingsimulations.model.navigablenode;

import nl.rug.modellingsimulations.model.VehicleBuffer;
import nl.rug.modellingsimulations.model.vehicle.Vehicle;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;

public class RoadNavigableNode extends VehicleBuffer {

    private List<NavigableNode> toNavigableNodes = new ArrayList<>();

    public RoadNavigableNode(int size) {
        super(size);
    }

    public void addNextNode(NavigableNode next) {
        this.toNavigableNodes.add(next);

        if(next instanceof JunctionLaneNavigableNode)
            ((JunctionLaneNavigableNode) next).setSourceRoad(this);
    }

    @Override
    public List<NavigableNode> getNextNodes() {
        return toNavigableNodes;
    }

}

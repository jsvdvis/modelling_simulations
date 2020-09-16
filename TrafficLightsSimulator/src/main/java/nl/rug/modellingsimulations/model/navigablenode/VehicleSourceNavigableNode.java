package nl.rug.modellingsimulations.model.navigablenode;


import nl.rug.modellingsimulations.model.VehicleBuffer;

import java.util.List;

public class VehicleSourceNavigableNode extends VehicleBuffer {

    private NavigableNode navigableNode;

    public VehicleSourceNavigableNode() {
        super(1);
    }

    @Override
    public List<NavigableNode> getNextNodes() {
        return List.of(navigableNode);
    }

    public void addNextNode(NavigableNode navigableNode) {
        this.navigableNode = navigableNode;
    }

}
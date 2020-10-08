package nl.rug.modellingsimulations.model.vehicle;

import nl.rug.modellingsimulations.model.navigablenode.NavigableNode;

public class NormalVehicle extends AbstractVehicle {

    public NormalVehicle(NavigableNode navigableNode) {
        super(navigableNode);
    }

    @Override
    public int getMaximumSpeed() {
        return 2;
    }
}

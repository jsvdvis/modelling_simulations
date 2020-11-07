package nl.rug.modellingsimulations.model.vehicle;

import nl.rug.modellingsimulations.model.navigablenode.NavigableNode;

public class FastVehicle extends AbstractVehicle {

    public FastVehicle(NavigableNode navigableNode) {
        super(navigableNode);
    }

    @Override
    public int getMaximumSpeed() {
        return 3;
    }
}

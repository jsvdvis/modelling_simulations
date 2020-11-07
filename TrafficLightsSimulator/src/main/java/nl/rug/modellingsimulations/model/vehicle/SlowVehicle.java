package nl.rug.modellingsimulations.model.vehicle;

import nl.rug.modellingsimulations.model.navigablenode.NavigableNode;
import nl.rug.modellingsimulations.model.vehicle.routingstrategy.RoutingStrategy;

public class SlowVehicle extends AbstractVehicle {

    public SlowVehicle(NavigableNode navigableNode) {
        super(navigableNode);
    }

    @Override
    public int getMaximumSpeed() {
        return 1;
    }
}

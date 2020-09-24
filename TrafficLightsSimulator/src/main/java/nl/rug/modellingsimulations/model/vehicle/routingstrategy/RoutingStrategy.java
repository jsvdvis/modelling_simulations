package nl.rug.modellingsimulations.model.vehicle.routingstrategy;

import nl.rug.modellingsimulations.model.navigablenode.NavigableNode;
import nl.rug.modellingsimulations.model.vehicle.Vehicle;

public interface RoutingStrategy {

    NavigableNode pickNextNode();

}

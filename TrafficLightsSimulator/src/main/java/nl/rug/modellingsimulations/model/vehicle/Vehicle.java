package nl.rug.modellingsimulations.model.vehicle;

import nl.rug.modellingsimulations.model.navigablenode.NavigableNode;
import nl.rug.modellingsimulations.model.vehicle.routingstrategy.RoutingStrategy;

public interface Vehicle {

    int getMaximumSpeed();
    int getCurrentSpeed();

    RoutingStrategy getRoutingStrategy();
    NavigableNode getNextNavigableNode();

    NavigableNode getCurrentNavigableNode();
    void setCurrentNavigableNode(NavigableNode navigableNode);

    boolean canMakeMove();
    void makeMove();

    void tryAccelerate();
    void fullBrake();

}

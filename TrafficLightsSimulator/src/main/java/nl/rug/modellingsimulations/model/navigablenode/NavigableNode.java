package nl.rug.modellingsimulations.model.navigablenode;

import nl.rug.modellingsimulations.model.vehicle.Vehicle;

import java.util.List;

/**
 * A navigable node is a location in our road graph that vehicles can go to or exit from.
 */
public interface NavigableNode {

    public void addNextNode(NavigableNode next);
    List<NavigableNode> getNextNodes();
    boolean canMovePosition(Vehicle vehicle);
    void movePosition(Vehicle vehicle);

}
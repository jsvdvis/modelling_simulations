package nl.rug.modellingsimulations.model.navigablenode;

import nl.rug.modellingsimulations.model.vehicle.Vehicle;
import nl.rug.modellingsimulations.utilities.Point;

import java.util.List;
import java.util.Set;

/**
 * A navigable node is a location in our road graph that vehicles can go to or exit from.
 */
public interface NavigableNode {

    List<NavigableNode> getNextNodes();
    void addNextNode(NavigableNode next);
    void removeNextNode(NavigableNode next);

    Set<NavigableNode> getPreviousNodes();
    void addPreviousNode(NavigableNode previous);
    void removePreviousNode(NavigableNode previous);

    boolean canMovePosition(Vehicle vehicle);
    void movePosition(Vehicle vehicle);
    Point getPosition(boolean precise);

}
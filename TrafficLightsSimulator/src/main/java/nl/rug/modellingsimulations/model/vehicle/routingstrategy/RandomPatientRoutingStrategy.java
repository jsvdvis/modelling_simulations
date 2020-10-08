package nl.rug.modellingsimulations.model.vehicle.routingstrategy;

import nl.rug.modellingsimulations.model.navigablenode.NavigableNode;
import nl.rug.modellingsimulations.model.vehicle.Vehicle;
import nl.rug.modellingsimulations.utilities.RandomGenerator;

import java.util.List;

public class RandomPatientRoutingStrategy implements RoutingStrategy {

    private final Vehicle vehicle;
    private NavigableNode nextNode = null;

    public RandomPatientRoutingStrategy(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    @Override
    public NavigableNode getNextNode() {
        List<NavigableNode> possibleNodes = vehicle.getCurrentNavigableNode().getNextNodes();

        // If we previously picked a next node to go to, we will stick with our choice.
        if(nextNode == null || !possibleNodes.contains(nextNode))
            // We have not picked a node yet, so now we pick one at random and stick to it.
            nextNode = RandomGenerator.getInstance().getRandomOfList(possibleNodes);

        return nextNode;
    }

}

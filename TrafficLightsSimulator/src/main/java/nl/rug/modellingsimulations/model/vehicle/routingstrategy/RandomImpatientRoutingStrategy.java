package nl.rug.modellingsimulations.model.vehicle.routingstrategy;

import nl.rug.modellingsimulations.model.navigablenode.NavigableNode;
import nl.rug.modellingsimulations.model.vehicle.Vehicle;
import nl.rug.modellingsimulations.utilities.RandomGenerator;

import java.util.List;

public class RandomImpatientRoutingStrategy implements RoutingStrategy {

    private final Vehicle vehicle;

    public RandomImpatientRoutingStrategy(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    @Override
    public NavigableNode getNextNode() {
        List<NavigableNode> possibleNodes = vehicle.getCurrentNavigableNode().getNextNodes();
        return RandomGenerator.getInstance().getRandomOfList(possibleNodes);
    }

}

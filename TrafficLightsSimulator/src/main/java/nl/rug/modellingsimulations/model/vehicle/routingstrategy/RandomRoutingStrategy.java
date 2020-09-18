package nl.rug.modellingsimulations.model.vehicle.routingstrategy;

import nl.rug.modellingsimulations.model.navigablenode.NavigableNode;
import nl.rug.modellingsimulations.model.vehicle.Vehicle;
import nl.rug.modellingsimulations.utilities.RandomGenerator;

import java.util.List;

public class RandomRoutingStrategy implements RoutingStrategy {

    private RandomRoutingStrategy instance = null;

    // Singleton
    private RandomRoutingStrategy(){}

    public RandomRoutingStrategy getInstance() {
        if(instance == null)
            instance = new RandomRoutingStrategy();
        return instance;
    }

    @Override
    public NavigableNode pickNextNode(Vehicle vehicle) {
        List<NavigableNode> possibleNodes = vehicle.getCurrentNavigableNode().getNextNodes();
        return RandomGenerator.getInstance().getRandomOfList(possibleNodes);
    }

}

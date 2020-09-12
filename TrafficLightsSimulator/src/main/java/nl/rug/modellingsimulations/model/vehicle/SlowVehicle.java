package nl.rug.modellingsimulations.model.vehicle;

import nl.rug.modellingsimulations.model.navigablenode.NavigableNode;
import nl.rug.modellingsimulations.model.vehicle.routingstrategy.RoutingStrategy;

public class SlowVehicle implements Vehicle {

    private int currentSpeed;
    private NavigableNode navigableNode;
    private RoutingStrategy routingStrategy;

    public SlowVehicle(NavigableNode navigableNode, RoutingStrategy routingStrategy) {
        this.currentSpeed = getMaximumSpeed();
        this.navigableNode = navigableNode;
        this.routingStrategy = routingStrategy;
    }

    @Override
    public int getMaximumSpeed() {
        return 1;
    }

    @Override
    public int getCurrentSpeed() {
        return currentSpeed;
    }

    @Override
    public RoutingStrategy getRoutingStrategy() {
        return routingStrategy;
    }

    @Override
    public NavigableNode getCurrentNavigableNode() {
        return navigableNode;
    }

    @Override
    public void setCurrentNavigableNode(NavigableNode navigableNode) {
        this.navigableNode = navigableNode;
    }

    @Override
    public boolean canMakeMove() {
        return navigableNode.canMovePosition(this);
    }

    @Override
    public void makeMove() {
        if(currentSpeed == 0)
            throw new IllegalStateException("Trying to move a vehicle that has no speed.");
        this.navigableNode.movePosition(this);
    }

    @Override
    public void tryAccelerate() {
        this.currentSpeed = Math.min(this.getMaximumSpeed(), this.getCurrentSpeed() + 1);
    }

    @Override
    public void fullBrake() {
        this.currentSpeed = 0;
    }

    @Override
    public NavigableNode getNextNavigableNode() {
        return this.getRoutingStrategy().pickNextNode(this);
    }
}

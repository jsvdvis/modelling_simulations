package nl.rug.modellingsimulations.model.vehicle;

import nl.rug.modellingsimulations.model.navigablenode.NavigableNode;
import nl.rug.modellingsimulations.model.vehicle.routingstrategy.RoutingStrategy;

public abstract class AbstractVehicle implements Vehicle {

    protected int currentSpeed;
    protected NavigableNode navigableNode;
    protected RoutingStrategy routingStrategy;
    protected int waitingTime = 0;

    protected AbstractVehicle(NavigableNode navigableNode) {
        this.currentSpeed = getMaximumSpeed();
        this.navigableNode = navigableNode;
    }

    public void setRoutingStrategy(RoutingStrategy routingStrategy) {
        this.routingStrategy = routingStrategy;
    }

    @Override
    public abstract int getMaximumSpeed();

    @Override
    public int getCurrentSpeed() {
        return currentSpeed;
    }

    @Override
    public RoutingStrategy getRoutingStrategy() {
        return this.routingStrategy;
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
        this.waitingTime = 0;
    }

    @Override
    public void tryAccelerate() {
        this.currentSpeed = Math.min(this.getMaximumSpeed(), this.getCurrentSpeed() + 1);
        this.waitingTime = 0;
    }

    @Override
    public void fullBrake() {
        this.currentSpeed = 0;
    }

    @Override
    public NavigableNode getNextNavigableNode() {
        return this.getRoutingStrategy().getNextNode();
    }

    public void setSpeed(int newSpeed) {
        this.currentSpeed = newSpeed;
    }

    @Override
    public void incrementWaitingTime() {
        this.waitingTime += 1;
    }

    @Override
    public int getWaitingTime() {
        return this.waitingTime;
    }
}

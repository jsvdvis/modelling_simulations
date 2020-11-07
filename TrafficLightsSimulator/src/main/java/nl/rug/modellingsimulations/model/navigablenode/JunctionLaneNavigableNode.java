package nl.rug.modellingsimulations.model.navigablenode;

import nl.rug.modellingsimulations.model.trafficlight.TrafficLightJunction;
import nl.rug.modellingsimulations.utilities.Point;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Each junctionlane is a small queue in front of a traffic light.
 * Here, vehicles will wait until the light goes green, before moving to the next node.
 */
public class JunctionLaneNavigableNode extends VehicleBuffer {

    private JunctionExitNavigableNode junctionExitNode;
    private TrafficLightJunction junction;
    private boolean isGreenLight = false;
    private Set<NavigableNode> previousNodes = new HashSet<>();

    public JunctionLaneNavigableNode(int size) {
        super(size);
    }

    @Override
    public void addNextNode(NavigableNode next) {
        if(!(next instanceof JunctionExitNavigableNode))
            throw new IllegalStateException("Junction Exit may only point to a Junction Exit.");

        this.junctionExitNode = (JunctionExitNavigableNode) next;
        this.junctionExitNode.addPreviousNode(this);
    }

    @Override
    public void removeNextNode(NavigableNode next) {
        this.junctionExitNode = null;
        next.removePreviousNode(this);
    }

    @Override
    public Set<NavigableNode> getPreviousNodes() {
        return previousNodes;
    }

    @Override
    public void addPreviousNode(NavigableNode previous) {
        this.previousNodes.add(previous);
    }

    @Override
    public void removePreviousNode(NavigableNode previous) {
        this.previousNodes.remove(previous);
    }

    @Override
    public List<NavigableNode> getNextNodes() {
        return List.of(junctionExitNode);
    }

    @Override
    public Point getPosition(boolean precise) {
        if(!precise)
            return this.getJunction().getPosition();

        return this.getJunction().getPositionOfLanesOrExit(this);
    }

    public boolean isGreenLight() {
        return isGreenLight;
    }

    public void setGreenLight(boolean isGreenLight) {
        this.isGreenLight = isGreenLight;
    }

    public RoadNavigableNode getSourceRoad() {
        return (RoadNavigableNode) previousNodes.iterator().next();
    }

    public NavigableNode getNodeBeforeSourceRoad() {
        if(previousNodes.size() != 1 || !(previousNodes.iterator().next() instanceof RoadNavigableNode))
            throw new IllegalStateException("The previous node(s) of the junction lane is not a single road!");

        RoadNavigableNode sourceRoad = getSourceRoad();

        if(sourceRoad.getPreviousNodes().size() > 1)
            throw new IllegalStateException("Unhandled case: Get source junction of lane when road has > 1 prev nodes.");

        return sourceRoad.getPreviousNodes().iterator().next();
    }

    public void setJunction(TrafficLightJunction junction) {
        this.junction = junction;
    }

    public TrafficLightJunction getJunction() {
        return junction;
    }

    public JunctionExitNavigableNode getJunctionExitNode() {
        return junctionExitNode;
    }
}

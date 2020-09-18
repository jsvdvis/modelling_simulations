package nl.rug.modellingsimulations.model.navigablenode;

import nl.rug.modellingsimulations.model.TrafficLightJunction;
import nl.rug.modellingsimulations.model.VehicleBuffer;
import nl.rug.modellingsimulations.utilities.Point;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Each junctionlane is a small queue in front of a traffic light.
 * Here, vehicles will wait until the light goes green, before moving to the next node.
 */
public class JunctionLaneNavigableNode extends VehicleBuffer {

    private RoadNavigableNode sourceRoad;
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
    public Set<NavigableNode> getPreviousNodes() {
        return previousNodes;
    }

    @Override
    public void addPreviousNode(NavigableNode previous) {
        this.previousNodes.add(previous);
    }

    @Override
    public List<NavigableNode> getNextNodes() {
        return List.of(junctionExitNode);
    }

    @Override
    public Point getPosition() {
        // Use relative position of junction
        throw new IllegalStateException("Not implemented.");
    }

    public boolean isGreenLight() {
        return isGreenLight;
    }

    public void setGreenLight(boolean isGreenLight) {
        this.isGreenLight = isGreenLight;
    }

    public RoadNavigableNode getSourceRoad() {
        return sourceRoad;
    }

    public void setSourceRoad(RoadNavigableNode sourceRoad) {
        this.sourceRoad = sourceRoad;
    }

    public void setJunction(TrafficLightJunction junction) {
        this.junction = junction;
    }

    public JunctionExitNavigableNode getJunctionExitNode() {
        return junctionExitNode;
    }
}

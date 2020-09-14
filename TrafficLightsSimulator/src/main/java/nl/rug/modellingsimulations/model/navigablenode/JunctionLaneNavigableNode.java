package nl.rug.modellingsimulations.model.navigablenode;

import nl.rug.modellingsimulations.model.VehicleBuffer;

import java.util.List;

/**
 * Each junctionlane is a small queue in front of a traffic light.
 * Here, vehicles will wait until the light goes green, before moving to the next node.
 */
public class JunctionLaneNavigableNode extends VehicleBuffer {

    private JunctionExitNavigableNode junctionExitNode;
    private boolean isGreenLight = false;

    public JunctionLaneNavigableNode(int size) {
        super(size);
    }

    @Override
    public List<NavigableNode> getNextNodes() {
        return List.of(junctionExitNode);
    }

    public boolean isGreenLight() {
        return isGreenLight;
    }

    public void setGreenLight(boolean isGreenLight) {
        this.isGreenLight = isGreenLight;
    }

}

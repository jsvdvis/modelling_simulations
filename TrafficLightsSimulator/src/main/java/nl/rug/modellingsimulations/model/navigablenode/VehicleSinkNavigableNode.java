package nl.rug.modellingsimulations.model.navigablenode;

import nl.rug.modellingsimulations.model.VehicleBuffer;
import nl.rug.modellingsimulations.model.vehicle.Vehicle;

import java.util.List;

public class VehicleSinkNavigableNode extends VehicleBuffer {

    protected VehicleSinkNavigableNode() {
        super(1);
    }

    @Override
    public List<NavigableNode> getNextNodes() {
        throw new IllegalStateException("Tried to get next node of a SinkNode.");
    }

    @Override
    public boolean canMovePosition(Vehicle vehicle) {
        throw new IllegalStateException("Tried to check if a vehicle can move inside a sink!");
    }

    @Override
    public void movePosition(Vehicle vehicle) {
        // TODO: void the vehicle
    }

}

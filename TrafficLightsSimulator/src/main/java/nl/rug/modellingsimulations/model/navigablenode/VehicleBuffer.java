package nl.rug.modellingsimulations.model.navigablenode;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import nl.rug.modellingsimulations.model.navigablenode.NavigableNode;
import nl.rug.modellingsimulations.model.navigablenode.VehicleSinkNavigableNode;
import nl.rug.modellingsimulations.model.vehicle.Vehicle;
import nl.rug.modellingsimulations.utilities.RandomGenerator;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class VehicleBuffer implements NavigableNode {

    private int size;
    private BiMap<Integer, Vehicle> vehicleSlots;

    protected VehicleBuffer(int size) {
        this.size = size;
        this.vehicleSlots = HashBiMap.create(this.size);
    }

    /**
     * Returns true if a vehicle is about to exit the buffer, false otherwise.
     * @param vehicle
     * @return
     */
    protected boolean isExitingBuffer(Vehicle vehicle) {
        return vehicleSlots.inverse().get(vehicle) == this.size - 1;
    }

    public boolean canMovePosition(Vehicle vehicle) {
        // Test if this is a new vehicle to the buffer
        if(!vehicleSlots.containsValue(vehicle)) {
            // The new vehicle can enter only if the first slot is empty.
            return !vehicleSlots.containsKey(0);
        }

        // If we are already in the buffer and in the last slot, we check if the next node has space for us!
        if(isExitingBuffer(vehicle)) {
            // If we are waiting before a traffic light, it must be green in order to be allowed to move
            if (this instanceof JunctionLaneNavigableNode) {
                JunctionLaneNavigableNode currentNode = (JunctionLaneNavigableNode) this;
                if (!currentNode.isGreenLight()) {
                    return false;
                }
                boolean canMovePosition = vehicle.getNextNavigableNode().canMovePosition(vehicle);
                if (canMovePosition) {
                    currentNode.getJunction().registerVehicleMovingThroughJunction();
                }
                return canMovePosition;
            }

            // This is not a traffic light. We can move as long as the next node's slot is free
            return vehicle.getNextNavigableNode().canMovePosition(vehicle);
        }

        // We can make a move if the next slot is available.
        return vehicleSlots.get(vehicleSlots.inverse().get(vehicle) + 1) == null;
    }

    /**
     * A vehicle wants to enter the buffer, or move to the next position in the buffer.
     * This method realizes that move.
     * @param vehicle
     */
    public void movePosition(Vehicle vehicle) {

        // STEP 1: Compute the next vehicle slot to move to
        int nextVehicleSlot;
        if(vehicleSlots.inverse().get(vehicle) == null)
            // Vehicle is entering buffer.
            nextVehicleSlot = 0;
        else if(isExitingBuffer(vehicle))
            // Vehicle is exiting buffer, set flag.
            nextVehicleSlot = -1;
        else
            // Vehicle is moving to the next slot in the buffer.
            nextVehicleSlot = vehicleSlots.inverse().get(vehicle) + 1;

        // Sanity check to see if the next slot is available
        if(nextVehicleSlot != -1 && vehicleSlots.get(nextVehicleSlot) != null) {
            throw new IllegalStateException("Trying to move a vehicle to the next slot, while it is not empty!");
        }

        // Recycle the old slot first!
        if(nextVehicleSlot != 0) {
            this.vehicleSlots.inverse().remove(vehicle);
        }

        // STEP 2: Make the move to the next slot
        if(nextVehicleSlot == -1) {
            // Moving out of the buffer, onto the next adventure!
            vehicle.getNextNavigableNode().movePosition(vehicle);

        } else {
            // We are making an internal move. Either a new vehicle in the buffer, or shifting a spot.
            // Moving to slot 0-size within the buffer. Updating buffer state.
            if(!(this instanceof VehicleSinkNavigableNode)) { // No need to use up a slot of a sink!
                vehicleSlots.put(nextVehicleSlot, vehicle);
            }
            vehicle.setCurrentNavigableNode(this);
        }
    }

    @Override
    // https://stackoverflow.com/a/22186845/3287095 rounding doubles
    public double getTrafficLoad() {
        double load = vehicleSlots.values().stream()
                .filter(Objects::nonNull)
                .count() / (double) this.size;
        int scale = (int) Math.pow(10, 1);
        return (double) Math.round(load * scale) / scale;
    }

    @Override
    public List<Vehicle> getVehicles() {
        return new ArrayList<>(this.vehicleSlots.values());
    }

}

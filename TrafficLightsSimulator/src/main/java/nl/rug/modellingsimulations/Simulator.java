package nl.rug.modellingsimulations;

import nl.rug.modellingsimulations.model.TrafficLightJunction;
import nl.rug.modellingsimulations.model.vehicle.Vehicle;
import nl.rug.modellingsimulations.simulation.Simulation;

import java.util.*;
import java.util.stream.Collectors;

public class Simulator {

    public Simulator() {}

    public void run(Simulation simulation) {
        int iteration = 0;
        while(true) {
            simulation = step(simulation);
            iteration += 1;
        }
    }

    /**
     * 1. Retrieve all TrafficLightJunctions
     * 2. Update the state (lights) of each TrafficLightJunction
     * 3. Retrieve all vehicles
     * 4. During each step, each vehicle has N possible actions (determined by speed).
     * 5a. Vehicles with 0 moves left, will accelerate if able to keep moving
     * 5b. All vehicles that have moves remaining but CANNOT make that move, have to break!
     * 5c. Get a list of movable vehicles
     * 5d. Move the vehicle, and reduce the maximum amount of movements remaining
     * 6. Accelerate all vehicles that did not brake and can still make a move
     */
    private Simulation step(Simulation simulation) {
        // TODO: deep copy of the simulation, so state transitions from one to the other
        simulation = simulation;

        // STEP 1: Retrieve traffic lights.
        List<TrafficLightJunction> trafficLightJunctions = simulation.getTrafficLightJunctions();

        // STEP 2: Update the state of all traffic lights
        trafficLightJunctions.parallelStream()
                .forEach(trafficLightJunction ->
                        trafficLightJunction.getTrafficLightStrategy().updateTrafficLights(trafficLightJunction)
                );

        // STEP 3: Retrieve all vehicles
        List<Vehicle> vehicles = simulation.getVehicles();

        // STEP 4: Determine the amount of actions each vehicle can take!
        Map<Vehicle, Integer> vehicleMovesAvailable = vehicles.parallelStream()
                .collect(Collectors.toMap(x -> x, Vehicle::getCurrentSpeed));

        Set<Vehicle> canMoveAfterIteration = new HashSet<>();
        while(vehicleMovesAvailable.size() > 0) {

            // STEP 5a: Vehicles with 0 moves left, will accelerate if able to keep moving
            vehicleMovesAvailable.entrySet()
                    .parallelStream()
                    .filter(x -> x.getValue() == 0 && x.getKey().canMakeMove())
                    .forEach(vehicle -> {
                        canMoveAfterIteration.add(vehicle.getKey());
                    });

            // STEP 5b: All vehicles that have moves remaining but CANNOT make that move, have to break!
            vehicleMovesAvailable.entrySet()
                    .parallelStream()
                    .filter(x -> x.getValue() > 0 && !x.getKey().canMakeMove())
                    .forEach(x -> x.getKey().fullBrake());

            // STEP 5c: Get a list of movable vehicles, since we want to perform all movements at the same time
            //   in order to avoid race conditions (only move available after the one in front moved)
            vehicleMovesAvailable = vehicleMovesAvailable.entrySet().parallelStream()
                    .filter(vehicleMoves -> vehicleMoves.getValue() > 0 && vehicleMoves.getKey().canMakeMove())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            // STEP 5d: Move the vehicle, and reduce the maximum amount of movements remaining!
            vehicleMovesAvailable = vehicleMovesAvailable.entrySet().parallelStream()
                    .map(vehicleMoves -> {
                        vehicleMoves.getKey().makeMove();
                        return new AbstractMap.SimpleEntry<>(vehicleMoves.getKey(), vehicleMoves.getValue()-1);
                    })
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        // Step 6: All vehicles that end up with an empty spot before them will accelerate in the next phase
        canMoveAfterIteration.forEach(Vehicle::tryAccelerate);

        return simulation;
    }

}

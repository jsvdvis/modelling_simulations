package nl.rug.modellingsimulations;

import nl.rug.modellingsimulations.model.TrafficLightJunction;
import nl.rug.modellingsimulations.model.vehicle.Vehicle;
import nl.rug.modellingsimulations.simulation.Simulation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
     * 5a. Get list of movable vehicles
     * 5b. Keep track of moved and unmoved vehicles during this iteration step
     * 5c. Move the vehicle and subtract available moves
     * 6. Accelerate all vehicles that could drive,
     *      OR that were able to accelerate at the start of the iteration
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

        Set<Vehicle> unmovedCanAccelerate = vehicles.parallelStream()
                .filter(Vehicle::canMakeMove).collect(Collectors.toSet());
        Set<Vehicle> movedVehicles = new HashSet<>();
        while(vehicleMovesAvailable.size() > 0) {
            // STEP 5a: Get a list of movable vehicles, since we want to perform all movements at the same time
            //   in order to avoid race conditions (only move available after the one in front moved)
            vehicleMovesAvailable = vehicleMovesAvailable.entrySet().parallelStream()
                    .filter(vehicleMoves -> vehicleMoves.getValue() > 0)
                    .filter(vehicleMoves -> vehicleMoves.getKey().canMakeMove())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            // STEP 5b: Remember the vehicles that we moved, so that we can perhaps increase the speed afterwards!
            vehicleMovesAvailable.keySet().forEach(vehicle -> {
                    unmovedCanAccelerate.remove(vehicle);
                    movedVehicles.add(vehicle);
            });

            // STEP 5c: Move the vehicle, and reduce the maximum amount of movements remaining!
            vehicleMovesAvailable = vehicleMovesAvailable.entrySet().parallelStream()
                    .map(vehicleMoves -> {
                        vehicleMoves.getKey().makeMove();
                        return new AbstractMap.SimpleEntry<>(vehicleMoves.getKey(), vehicleMoves.getValue()-1);
                    })
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        // Step 6: All vehicles that could NOT move but COULD accelerate,
        //  OR all vehicles that were driving, will now try to accelerate!
        unmovedCanAccelerate.forEach(Vehicle::tryAccelerate);
        movedVehicles.forEach(Vehicle::tryAccelerate);

        return simulation;
    }

}

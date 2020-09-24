package nl.rug.modellingsimulations;

import nl.rug.modellingsimulations.config.SimulatorConfig;
import nl.rug.modellingsimulations.graph.GraphMediator;
import nl.rug.modellingsimulations.graph.GraphStreamMediator;
import nl.rug.modellingsimulations.model.TrafficLightJunction;
import nl.rug.modellingsimulations.model.navigablenode.VehicleSinkNavigableNode;
import nl.rug.modellingsimulations.model.navigablenode.VehicleSourceNavigableNode;
import nl.rug.modellingsimulations.model.vehicle.AbstractVehicle;
import nl.rug.modellingsimulations.model.vehicle.SlowVehicle;
import nl.rug.modellingsimulations.model.vehicle.Vehicle;
import nl.rug.modellingsimulations.model.vehicle.routingstrategy.RandomRoutingStrategy;
import nl.rug.modellingsimulations.model.vehicle.routingstrategy.RoutingStrategy;
import nl.rug.modellingsimulations.simulation.Simulation;
import nl.rug.modellingsimulations.utilities.RandomGenerator;

import java.util.*;
import java.util.stream.Collectors;

public class Simulator {

    Simulation simulation;
    int currentIteration = 0;

    public Simulator(Simulation simulation) {
        this.simulation = simulation;
    }

    public void run() {
        GraphMediator graphMediator = new GraphStreamMediator(simulation);
        graphMediator.createGraph();

        while(true) {
            this.simulation = step();
            graphMediator.updateView();
            graphMediator.updateView();

            try {
                Thread.sleep(SimulatorConfig.getSleepBetweenStepMs());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            currentIteration += 1;
        }
    }

    /**
     * 1. Retrieve all TrafficLightJunctions
     * 2. Update the state (lights) of each TrafficLightJunction
     * 3. Retrieve all vehicles
     * 4. During each step, each vehicle has N possible actions (determined by speed).
     * 5. Create vehicles at the sources if chance allows
     * 6a. Vehicles with 0 moves left, will accelerate if able to keep moving
     * 6b. All vehicles that have moves remaining but CANNOT make that move, have to break!
     * 6c. Get a list of movable vehicles
     * 6d. Move the vehicle, and reduce the maximum amount of movements remaining
     * 6e. For each vehicle sink, remove the vehicle, if any
     * 6f. Replenish the vehicle sources, if required
     * 7. Accelerate all vehicles that did not brake and can still make a move
     */
    private Simulation step() {
        // TODO: deep copy of the simulation, so state transitions from one to the other
        // simulation = simulation;

        // STEP 1: Retrieve traffic lights.
        List<TrafficLightJunction> trafficLightJunctions = simulation.getTrafficLightJunctions();

        // STEP 2: Update the state of all traffic lights
        trafficLightJunctions.parallelStream()
                .forEach(trafficLightJunction ->
                        trafficLightJunction.getTrafficLightStrategy().updateTrafficLights()
                );

        // STEP 3: Retrieve all vehicles
        List<Vehicle> vehicles = simulation.getVehicles();

        // STEP 4: Determine the amount of actions each vehicle can take!
        Map<Vehicle, Integer> vehicleMovesAvailable = vehicles.parallelStream()
                .collect(Collectors.toMap(x -> x, Vehicle::getCurrentSpeed));

        // STEP 5: Create vehicles at the sources
        generateVehiclesAtSources(-1);

        Set<Vehicle> canMoveAfterIteration = new HashSet<>();
        while(vehicleMovesAvailable.size() > 0) {

            // STEP 6a: Vehicles with 0 moves left, will accelerate if able to keep moving
            vehicleMovesAvailable.entrySet()
                    .parallelStream()
                    .filter(x -> x.getValue() == 0 && x.getKey().canMakeMove())
                    .forEach(vehicle -> {
                        canMoveAfterIteration.add(vehicle.getKey());
                    });

            // STEP 6b: All vehicles that have moves remaining but CANNOT make that move, have to break!
            vehicleMovesAvailable.entrySet()
                    .parallelStream()
                    .filter(x -> x.getValue() > 0 && !x.getKey().canMakeMove())
                    .forEach(x -> x.getKey().fullBrake());

            // STEP 6c: Get a list of movable vehicles, since we want to perform all movements at the same time
            //   in order to avoid race conditions (only move available after the one in front moved)
            vehicleMovesAvailable = vehicleMovesAvailable.entrySet().parallelStream()
                    .filter(vehicleMoves -> vehicleMoves.getValue() > 0 && vehicleMoves.getKey().canMakeMove())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            // STEP 6d: Move the vehicle, and reduce the maximum amount of movements remaining!
            vehicleMovesAvailable = vehicleMovesAvailable.entrySet().stream()
                    .map(vehicleMoves -> {
                        try {
                            vehicleMoves.getKey().makeMove();
                        } catch (IllegalStateException e) {
                            vehicleMoves.getKey().fullBrake();
                            return new AbstractMap.SimpleEntry<>(vehicleMoves.getKey(), 0);
                        }
                        return new AbstractMap.SimpleEntry<>(vehicleMoves.getKey(), vehicleMoves.getValue()-1);
                    })
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            // Step 6e: Remove all vehicles that are in the sink, if any
            vehicleMovesAvailable.keySet().stream()
                    .filter(vehicle -> vehicle.getCurrentNavigableNode() instanceof VehicleSinkNavigableNode)
                    .forEach(vehicle -> simulation.removeVehicle(vehicle));

            // STEP 6f: Replenish the sources with new vehicles, if necessary
            int maxSpeed = vehicleMovesAvailable.values().stream().mapToInt(x -> x).max().orElse(-1);
            generateVehiclesAtSources(maxSpeed);
        }

        // Step 7: All vehicles that end up with an empty spot before them will accelerate in the next phase
        canMoveAfterIteration.forEach(Vehicle::tryAccelerate);

        return simulation;
    }

    private void generateVehiclesAtSources(int maxSpeed) {
        double chanceOfSpawn = SimulatorConfig.getPercentChanceVehicleSpawnsAtSource();
        simulation.getSources().forEach(vehicleSource -> {
            int chanceDraw = RandomGenerator.getInstance().getIntegerBetween(0, 100);
            if(chanceDraw <= chanceOfSpawn) {
                // Add a vehicle to this source!
                createRandomVehicle(vehicleSource, maxSpeed);
            }
        });
    }

    private void createRandomVehicle(VehicleSourceNavigableNode vehicleSource, int maxSpeed) {

        if(vehicleSource.getTrafficLoad() >= 0.999)
            return;

        Map<Class<? extends AbstractVehicle>, Integer> vehicleChanceMap = SimulatorConfig.getVehicleChanceTypeMap();
        int chanceDraw = RandomGenerator.getInstance().getIntegerBetween(0, 100);

        int sumOfChances = 0;
        for(Map.Entry<Class<? extends AbstractVehicle>, Integer> vehicleChance : vehicleChanceMap.entrySet()) {
            sumOfChances += vehicleChance.getValue();
            if(chanceDraw <= sumOfChances) {

                // Create new vehicle of this type!
                Vehicle vehicle = null;
                if(vehicleChance.getKey().equals(SlowVehicle.class)) {
                    vehicle = new SlowVehicle(vehicleSource);
                    vehicle.setRoutingStrategy(createRandomRoutingStrategy(vehicle));
                    if(vehicle.getCurrentSpeed() > maxSpeed && maxSpeed != -1)
                        vehicle.setSpeed(maxSpeed);
                } // Add other vehicle types here

                simulation.addNewVehicle(vehicle);
                return;
            }
        }
    }

    private RoutingStrategy createRandomRoutingStrategy(Vehicle vehicle) {
        Map<Class<? extends RoutingStrategy>, Integer> routingStrategyChanceMap = SimulatorConfig.getRoutingChanceTypeMap();
        int chanceDraw = RandomGenerator.getInstance().getIntegerBetween(0, 100);

        int sumOfChances = 0;
        for(Map.Entry<Class<? extends RoutingStrategy>, Integer> stratChance : routingStrategyChanceMap.entrySet()) {
            sumOfChances += stratChance.getValue();

            if(chanceDraw <= sumOfChances) {

                // Create new vehicle of this type!
                RoutingStrategy routingStrategy = null;
                if(stratChance.getKey().equals(RandomRoutingStrategy.class)) {
                    routingStrategy = new RandomRoutingStrategy(vehicle);
                } // Add other vehicle types here

                return routingStrategy;
            }
        }
        return null;
    }

}

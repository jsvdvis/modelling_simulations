package nl.rug.modellingsimulations;

import nl.rug.modellingsimulations.config.SimulatorConfig;
import nl.rug.modellingsimulations.graph.GraphMediator;
import nl.rug.modellingsimulations.graph.GraphStreamMediator;
import nl.rug.modellingsimulations.metrics.*;
import nl.rug.modellingsimulations.model.trafficlight.TrafficLightJunction;
import nl.rug.modellingsimulations.model.navigablenode.VehicleSinkNavigableNode;
import nl.rug.modellingsimulations.model.navigablenode.VehicleSourceNavigableNode;
import nl.rug.modellingsimulations.model.vehicle.*;
import nl.rug.modellingsimulations.model.vehicle.routingstrategy.*;
import nl.rug.modellingsimulations.simulation.Simulation;
import nl.rug.modellingsimulations.utilities.RandomGenerator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Simulator {

    Simulation simulation;
    int currentIteration = 0;
    private AveragedStoppedVehicles stoppedVehicles;
    private AveragedVehicleWaitingTime vehicleWaitingTime;
    private boolean shouldDisplay = true;

    public Simulator(Simulation simulation) {
        this.simulation = simulation;
    }

    public void run() {
        GraphMediator graphMediator = null;
        if (shouldDisplay) {
            graphMediator = new GraphStreamMediator(simulation);
            graphMediator.createGraph();
        }

        // Register metrics-measurers
        MetricsStepResultSaver throughputPrinter = new CsvFileSaver("throughput");
        AveragedTrafficLightJunctionThroughput junctionThroughput = new AveragedTrafficLightJunctionThroughput(throughputPrinter);
        simulation.getTrafficLightJunctions().forEach(junction -> junction.setThroughputMeasurer(junctionThroughput));

        MetricsStepResultSaver stoppedVehiclePrinter = new CsvFileSaver("stopped");
        stoppedVehicles = new AveragedStoppedVehicles(stoppedVehiclePrinter);

        MetricsStepResultSaver vehicleWaitingTimePrinter = new CsvFileSaver("waiting_time");
        vehicleWaitingTime = new AveragedVehicleWaitingTime(vehicleWaitingTimePrinter);

        long timer;
        while(true) {
            timer = System.currentTimeMillis();

            junctionThroughput.initSimulationStep();
            stoppedVehicles.initSimulationStep();
            vehicleWaitingTime.initSimulationStep();
            this.simulation = step();
            junctionThroughput.finishSimulationStep(this.simulation);
            stoppedVehicles.finishSimulationStep(this.simulation);
            vehicleWaitingTime.finishSimulationStep(this.simulation);

            timer = System.currentTimeMillis() - timer;
            System.out.println("Iteration: " + currentIteration + " completed in: " + timer + " milliseconds.");

            // Sleeping before the view on purpose, so the view is always being updated after the same interval amount!
            if (shouldDisplay && graphMediator != null && (
                    currentIteration > SimulatorConfig.getIterationsBeforeDisplayGraph()
                    || currentIteration % 1000 == 0
            )) {
                try {
                    timer = SimulatorConfig.getSleepBetweenStepMs() - timer; // Time to sleep
                    if(timer > 0)
                        Thread.sleep(timer);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                graphMediator.updateView();
            }
            currentIteration += 1;
        }
    }

    /**
     * 1. Retrieve all TrafficLightJunctions
     * 2. Update the state (lights) of each TrafficLightJunction
     * 3. Retrieve all vehicles
     * 4. Update the impatience of all our A* vehicles
     * 5. For each vehicle, determine the number of allowed movements (determined by speed)
     * 6. Create vehicles at the sources if chance allows
     * 7. As long as vehicles can still make moves, do the following
     * 7a. Put Vehicles with 0 moves left on a list to accelerate, if their next movement is not hindered
     * 7b. Increment waiting time of Vehicles with 0 moves left and currentSpeed 0 that cannot accelerate.
     * 7c. All vehicles that have moves remaining but CANNOT make that move, have to break!
     * 7d. Get a list of movable vehicles
     * 7e. Move the vehicle, and reduce the maximum amount of movements remaining
     * 7f. For each vehicle sink, remove the vehicle, if any
     * 7g. Replenish the vehicle sources, if required
     * 8. Accelerate all vehicles that did not brake during this step and can still make a move
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
        System.out.println("Vehicles in Sim: " + simulation.getVehicles().size());
        System.out.println("Vehicles in nodes: " + simulation.getNodes().stream().mapToLong(x -> x.getVehicles().size()).sum());

        // STEP 4: Increase the impatience of our A* routing vehicles!
        vehicles.parallelStream()
                .filter(vehicle -> vehicle.getRoutingStrategy() instanceof AStarSwitchingWeightedRoutingStrategy
                    || vehicle.getRoutingStrategy() instanceof AStarSwitchingRoutingStrategy)
                .forEach(vehicle -> {
                    if(vehicle.getRoutingStrategy() instanceof AStarSwitchingRoutingStrategy)
                        ((AStarSwitchingRoutingStrategy) vehicle.getRoutingStrategy()).updateImpatience();
                    else
                        ((AStarSwitchingWeightedRoutingStrategy) vehicle.getRoutingStrategy()).updateImpatience();
                });

        // STEP 5: Determine the amount of actions each vehicle can take!
        Map<Vehicle, Integer> vehicleMovesAvailable = vehicles.parallelStream()
                .collect(Collectors.toMap(x -> x, Vehicle::getCurrentSpeed));

        // STEP 6: Create vehicles at the sources
        generateVehiclesAtSources(-1);

        Set<Vehicle> canMoveAfterIteration = Collections.newSetFromMap(new ConcurrentHashMap<>());
        // STEP 7: As long as vehicles can still make moves, do the following
        while(vehicleMovesAvailable.size() > 0) {

            // STEP 7a: Put Vehicles with 0 moves left on a list to accelerate, if their next movement is not hindered
            vehicleMovesAvailable.entrySet()
                    .parallelStream()
                    .filter(x -> x.getValue() == 0 && x.getKey().canMakeMove())
                    .forEach(vehicle -> {
                        canMoveAfterIteration.add(vehicle.getKey());
                    });

            // STEP 7b: Increment waiting time of Vehicles that have 0 moves left, cannot accelerate and 0 currentSpeed
            vehicleMovesAvailable.entrySet()
                    .parallelStream()
                    .filter(
                            x -> x.getValue() == 0
                            && x.getKey().getCurrentSpeed() == 0
                            && !canMoveAfterIteration.contains(x.getKey())
                    )
                    .forEach(vehicle -> vehicle.getKey().incrementWaitingTime());

            // STEP 7c: All vehicles that have moves remaining but CANNOT make that move, have to break!
            vehicleMovesAvailable.entrySet()
                    .parallelStream()
                    .filter(x -> x.getValue() > 0 && !x.getKey().canMakeMove())
                    .forEach(x -> x.getKey().fullBrake());

            // STEP 7d: Get a list of movable vehicles, since we want to perform all movements at the same time
            //   in order to avoid race conditions (only move available after the one in front moved)
            vehicleMovesAvailable = vehicleMovesAvailable.entrySet().parallelStream()
                    .filter(vehicleMoves -> vehicleMoves.getValue() > 0 && vehicleMoves.getKey().canMakeMove())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            // STEP 7e: Move the vehicle, and reduce the maximum amount of movements remaining!
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

            // Step 7f: Remove all vehicles that are in the sink, if any
            vehicleMovesAvailable.keySet().stream()
                    .filter(vehicle -> vehicle.getCurrentNavigableNode() instanceof VehicleSinkNavigableNode)
                    .forEach(vehicle -> simulation.removeVehicle(vehicle));

            // STEP 7g: Replenish the sources with new vehicles, if necessary
            int maxSpeed = vehicleMovesAvailable.values().stream().mapToInt(x -> x).max().orElse(-1);
            generateVehiclesAtSources(maxSpeed);
        }

        // Step 8: All vehicles that end up with an empty spot before them will accelerate in the next phase
        canMoveAfterIteration.forEach(Vehicle::tryAccelerate);


        // MEASUREMENT STEP
        stoppedVehicles.addStoppedVehicleCount(
                (int)simulation.getVehicles().stream().filter(vehicle ->
                        vehicle.getCurrentSpeed() == 0
                ).count()
        );
        simulation.getVehicles().forEach(vehicle ->
                vehicleWaitingTime.addVehicleWaitingTime(vehicle)
        );

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
                } else if (vehicleChance.getKey().equals(NormalVehicle.class)) {
                    vehicle = new NormalVehicle(vehicleSource);
                    vehicle.setRoutingStrategy(createRandomRoutingStrategy(vehicle));
                    if(vehicle.getCurrentSpeed() > maxSpeed && maxSpeed != -1)
                        vehicle.setSpeed(maxSpeed);
                } else if (vehicleChance.getKey().equals(FastVehicle.class)) {
                    vehicle = new FastVehicle(vehicleSource);
                    vehicle.setRoutingStrategy(createRandomRoutingStrategy(vehicle));
                    if(vehicle.getCurrentSpeed() > maxSpeed && maxSpeed != -1)
                        vehicle.setSpeed(maxSpeed);
                }
                // Add other vehicle types here

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
                if(stratChance.getKey().equals(RandomImpatientRoutingStrategy.class)) {
                    routingStrategy = new RandomImpatientRoutingStrategy(vehicle);
                }  else if(stratChance.getKey().equals(RandomPatientRoutingStrategy.class)) {
                    routingStrategy = new RandomPatientRoutingStrategy(vehicle);
                } else if(stratChance.getKey().equals(AStarSwitchingRoutingStrategy.class)) {
                    routingStrategy = new AStarSwitchingRoutingStrategy(vehicle);
                } else if(stratChance.getKey().equals(AStarSwitchingWeightedRoutingStrategy.class)) {
                    routingStrategy = new AStarSwitchingWeightedRoutingStrategy(vehicle);
                }
                // Add other vehicle strats here

                return routingStrategy;
            }
        }
        return null;
    }

    public void hideDisplay() {
        this.shouldDisplay = false;
    }
}

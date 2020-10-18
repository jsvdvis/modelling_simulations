package nl.rug.modellingsimulations.config;

import nl.rug.modellingsimulations.model.vehicle.AbstractVehicle;
import nl.rug.modellingsimulations.model.vehicle.FastVehicle;
import nl.rug.modellingsimulations.model.vehicle.NormalVehicle;
import nl.rug.modellingsimulations.model.vehicle.SlowVehicle;
import nl.rug.modellingsimulations.model.vehicle.routingstrategy.*;

import java.util.Map;

public class SimulatorConfig {

    private static final long SLEEP_BETWEEN_STEP_MS = 400;
    private static final double PERCENT_CHANCE_VEHICLE_SPAWNS_AT_SOURCE = 2;
    private static final long ITERATIONS_BEFORE_DISPLAY_GRAPH = 10000;

    // After adding here, make sure to update Simulator.class#createRandomVehicle
    private static final Map<Class<? extends AbstractVehicle>, Integer> VEHICLE_CHANCE_TYPE_MAP = Map.of(
            SlowVehicle.class, 25,
            NormalVehicle.class, 50,
            FastVehicle.class, 25
    );

    // After adding here, make sure to update Simulator.class#createRandomRoutingStrategy
    private static final Map<Class<? extends RoutingStrategy>, Integer> ROUTING_CHANCE_TYPE_MAP = Map.of(
            RandomImpatientRoutingStrategy.class, 0,
            RandomPatientRoutingStrategy.class, 0,
            AStarSwitchingRoutingStrategy.class, 0,
            AStarSwitchingWeightedRoutingStrategy.class, 100
    );

    private SimulatorConfig(){}

    public static long getSleepBetweenStepMs() {
        return SLEEP_BETWEEN_STEP_MS;
    }

    public static double getPercentChanceVehicleSpawnsAtSource() {
        return PERCENT_CHANCE_VEHICLE_SPAWNS_AT_SOURCE;
    }

    public static Map<Class<? extends AbstractVehicle>, Integer> getVehicleChanceTypeMap() {
        return VEHICLE_CHANCE_TYPE_MAP;
    }

    public static Map<Class<? extends RoutingStrategy>, Integer> getRoutingChanceTypeMap() {
        return ROUTING_CHANCE_TYPE_MAP;
    }

    public static long getIterationsBeforeDisplayGraph() {
        return ITERATIONS_BEFORE_DISPLAY_GRAPH;
    }
}

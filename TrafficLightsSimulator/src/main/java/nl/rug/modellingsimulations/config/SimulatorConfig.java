package nl.rug.modellingsimulations.config;

import nl.rug.modellingsimulations.model.vehicle.AbstractVehicle;
import nl.rug.modellingsimulations.model.vehicle.SlowVehicle;
import nl.rug.modellingsimulations.model.vehicle.Vehicle;
import nl.rug.modellingsimulations.model.vehicle.routingstrategy.RandomRoutingStrategy;
import nl.rug.modellingsimulations.model.vehicle.routingstrategy.RoutingStrategy;

import java.util.HashMap;
import java.util.Map;

public class SimulatorConfig {

    private static final long SLEEP_BETWEEN_STEP_MS = 100;
    private static final double PERCENT_CHANCE_VEHICLE_SPAWNS_AT_SOURCE = 100;
    private static final Map<Class<? extends AbstractVehicle>, Integer> VEHICLE_CHANCE_TYPE_MAP = Map.of(
            SlowVehicle.class, 100
    );

    private static final Map<Class<? extends RoutingStrategy>, Integer> ROUTING_CHANCE_TYPE_MAP = Map.of(
            RandomRoutingStrategy.class, 100
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
}

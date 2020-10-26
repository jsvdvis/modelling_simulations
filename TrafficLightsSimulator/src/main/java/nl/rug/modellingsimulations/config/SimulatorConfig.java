package nl.rug.modellingsimulations.config;

import nl.rug.modellingsimulations.model.trafficlight.trafficlightstrategy.*;
import nl.rug.modellingsimulations.model.vehicle.AbstractVehicle;
import nl.rug.modellingsimulations.model.vehicle.FastVehicle;
import nl.rug.modellingsimulations.model.vehicle.NormalVehicle;
import nl.rug.modellingsimulations.model.vehicle.SlowVehicle;
import nl.rug.modellingsimulations.model.vehicle.routingstrategy.*;

import java.util.Map;

public class SimulatorConfig {

    /**
     * During the simulation, we often want the program to wait a little while before going to the next step.
     * Time should be set in milliseconds.
     * Default: 400
     */
    private static final long SLEEP_BETWEEN_STEP_MS = 400;

    /**
     * This mainly determines the traffic density in percentage.
     * A value of 100 means every source will always try to push new vehicles in the network whenever it can.
     * A value of 5 means every source will try to push new vehicles in the network once every 20 times.
     * Default: 25
     */
    private static final double PERCENT_CHANCE_VEHICLE_SPAWNS_AT_SOURCE = 25;

    /**
     * Sometimes, we want to skip initialization of the network when no vehicles have entered yet.
     * Using this parameters, we can control how many iterations to jump ahead, before starting visualization.
     * Setting this to 0 means one can observe the network from when it is completely empty.
     * Default: 10.000
     */
    private static final long ITERATIONS_BEFORE_DISPLAY_GRAPH = 10000;

    /**
     * Maps the type of vehicle to the percentage it should occur in the network.
     * Determines the network composition of road users.
     * Sum should always be 100.
     * After adding a new vehicle here, make sure to update Simulator.class#createRandomVehicle
     * Default: Slow -> 25, Normal -> 50, Fast -> 25.
     */
    private static final Map<Class<? extends AbstractVehicle>, Integer> VEHICLE_CHANCE_TYPE_MAP = Map.of(
            SlowVehicle.class, 25,
            NormalVehicle.class, 50,
            FastVehicle.class, 25
    );

    /**
     * Maps the type of routing strategy to the percentage of vehicles that should use it.
     * This models the behaviour of the road users.
     * Sum should always be 100.
     * After adding a new strategy here, make sure to update Simulator.class#createRandomRoutingStrategy
     * Default: no default, highly depends on goal.
     */
    private static final Map<Class<? extends RoutingStrategy>, Integer> ROUTING_CHANCE_TYPE_MAP = Map.of(
            RandomImpatientRoutingStrategy.class, 0,
            RandomPatientRoutingStrategy.class, 0,
            AStarSwitchingRoutingStrategy.class, 0,
            AStarSwitchingWeightedRoutingStrategy.class, 100
    );

    /**
     * Maps the type of routing strategy to the percentage of vehicles that should use it.
     * This models the behaviour of the road users.
     * Sum should always be 100.
     * After adding a new strategy here, make sure to update Simulator.class#createRandomRoutingStrategy
     * Default: no default, highly depends on goal.
     */
    private static final Map<Class<? extends TrafficLightStrategy>, Integer> TRAFFIC_LIGHT_STRAT_CHANCE_TYPE_MAP = Map.of(
            TimedSidedRoundRobinTrafficLightStrategy.class, 0,
            SensoredSidedRoundRobinTrafficLightStrategy.class, 0,
            SensoredFifoTrafficLightStrategy.class, 0,
            RadarWeightedTrafficLightStrategy.class, 100
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

    public static Map<Class<? extends TrafficLightStrategy>, Integer> getTrafficLightStratChanceTypeMap() {
        return TRAFFIC_LIGHT_STRAT_CHANCE_TYPE_MAP;
    }
}

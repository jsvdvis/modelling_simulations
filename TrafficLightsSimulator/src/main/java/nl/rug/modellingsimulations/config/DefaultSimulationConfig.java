package nl.rug.modellingsimulations.config;

import nl.rug.modellingsimulations.utilities.RandomGenerator;

public class DefaultSimulationConfig implements SimulationConfig {

    private final RandomGenerator random = RandomGenerator.getInstance();
    private static DefaultSimulationConfig instance = null;

    private final int TRAFFIC_LIGHT_LANE_SIZE_MIN = 1;
    private final int TRAFFIC_LIGHT_LANE_SIZE_MAX = 6;

    public static SimulationConfig getInstance() {
        if(instance == null)
            instance = new DefaultSimulationConfig();
        return instance;
    }

    public int getRandomLaneSize() {
        return random.getIntegerBetween(TRAFFIC_LIGHT_LANE_SIZE_MIN, TRAFFIC_LIGHT_LANE_SIZE_MAX);
    }

}

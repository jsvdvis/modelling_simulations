package nl.rug.modellingsimulations.config;

import nl.rug.modellingsimulations.utilities.RandomGenerator;

public class DefaultSimulationConfig implements SimulationConfig {

    private static final RandomGenerator random = RandomGenerator.getInstance();
    private static DefaultSimulationConfig instance = null;

    private static final int JUNCTION_LANE_SIZE_MIN = 6;
    private static final int JUNCTION_LANE_SIZE_MAX = 6;

    private DefaultSimulationConfig() {}

    public static SimulationConfig getInstance() {
        if(instance == null)
            instance = new DefaultSimulationConfig();
        return instance;
    }

    public int getRandomLaneSize() {
        return random.getIntegerBetween(JUNCTION_LANE_SIZE_MIN, JUNCTION_LANE_SIZE_MAX);
    }


}

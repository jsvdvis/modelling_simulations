package nl.rug.modellingsimulations.config;

import nl.rug.modellingsimulations.utilities.RandomGenerator;

public class DefaultSimulationConfig implements SimulationConfig {

    private static final RandomGenerator random = RandomGenerator.getInstance();
    private static DefaultSimulationConfig instance = null;

    /**
     * In case junction lanes vary, below parameters can be used to determine
     * the upper and lower bound of how many vehicles can be in a single junction lane before a traffic light.
     * The actual number that will be used is randomly selected between this interval
     * Default: [6,6]
     */
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

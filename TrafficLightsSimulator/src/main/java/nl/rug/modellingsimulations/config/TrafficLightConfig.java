package nl.rug.modellingsimulations.config;

public class TrafficLightConfig {

    private final static int MINIMUM_TIME_GREEN_LIGHT = 10; // in iterations

    public static int getMinimumTimeGreenLight() {
        return MINIMUM_TIME_GREEN_LIGHT;
    }
}

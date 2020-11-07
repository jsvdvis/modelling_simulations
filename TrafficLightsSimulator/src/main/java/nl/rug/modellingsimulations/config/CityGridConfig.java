package nl.rug.modellingsimulations.config;

public class CityGridConfig {

    /**
     * Distance between any 2 junctions in a city grid.
     * This defines the length of a single road between them, as well as how many cars can be on it.
     * Default: 16, meaning there can be 16 vehicles on the road between 2 junctions.
     */
    private static final int SPACING_BETWEEN_JUNCTIONS = 16;

    public static int getSpacingBetweenJunctions() {
        return SPACING_BETWEEN_JUNCTIONS;
    }
}

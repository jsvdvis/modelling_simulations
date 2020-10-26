package nl.rug.modellingsimulations.config;

public class JunctionSpacingConfig {

    /**
     * The junction lane offset describes the spacing between exits and lanes on the same side of a junction.
     * Default: 0.35
     */
    private static final double junctionLaneExitOffset = 0.35;

    /**
     * This describes how far the "group of exits and lanes" must be from the center of the junction itself
     * Default: 0.15
     */
    private static final double sameSideNodeOffset = 0.15;

    private JunctionSpacingConfig(){}

    public static double getJunctionLaneExitOffset() {
        return junctionLaneExitOffset;
    }

    public static double getSameSideNodeOffset() {
        return sameSideNodeOffset;
    }
}

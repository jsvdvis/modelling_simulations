package nl.rug.modellingsimulations.config;

public class JunctionSpacingConfig {

    private static final double junctionLaneExitOffset = 0.3;
    private static final double sameSideNodeOffset = 0.1;

    public static double getJunctionLaneExitOffset() {
        return junctionLaneExitOffset;
    }

    public static double getSameSideNodeOffset() {
        return sameSideNodeOffset;
    }
}

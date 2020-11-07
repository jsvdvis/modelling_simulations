package nl.rug.modellingsimulations.utilities;

import nl.rug.modellingsimulations.model.trafficlight.TrafficLightJunction;

import java.util.Comparator;

public class SortByJunctionDirection implements Comparator<TrafficLightJunction> {
    private final TrafficLightJunction source;

    public SortByJunctionDirection(TrafficLightJunction source) {
        this.source = source;
    }

    @Override
    public int compare(TrafficLightJunction o1, TrafficLightJunction o2) {
        return Double.compare(
                source.getPosition().getAngle(o1.getPosition()),
                source.getPosition().getAngle(o2.getPosition())
        );
    }
}

package nl.rug.modellingsimulations.model;

import nl.rug.modellingsimulations.model.trafficlightstrategy.TrafficLightStrategy;
import nl.rug.modellingsimulations.utilities.Point;

/**
 * A simple junction with no sensors
 */
public class SimpleTrafficLightJunction extends TrafficLightJunction {

    public SimpleTrafficLightJunction(Point point) {
        super(point);
    }

}

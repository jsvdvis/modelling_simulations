package nl.rug.modellingsimulations.model.trafficlightstrategy;

import nl.rug.modellingsimulations.model.TrafficLightJunction;

/**
 * A traffic light strategy is an algorithm that determines the behaviour of traffic lights
 */
public interface TrafficLightStrategy {

    void updateTrafficLights();

}

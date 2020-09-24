package nl.rug.modellingsimulations.model.trafficlightstrategy;

import nl.rug.modellingsimulations.model.TrafficLightJunction;

public class RoundRobinTimerTrafficLightStrategy implements TrafficLightStrategy {

    private final TrafficLightJunction trafficLightJunction;

    public RoundRobinTimerTrafficLightStrategy(TrafficLightJunction trafficLightJunction) {
        this.trafficLightJunction = trafficLightJunction;
    }

    @Override
    public void updateTrafficLights() {
    }

}

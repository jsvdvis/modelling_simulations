package nl.rug.modellingsimulations.model.trafficlight.trafficlightstrategy;

import nl.rug.modellingsimulations.model.trafficlight.TrafficLightJunction;

public class RoundRobinTimerTrafficLightStrategy implements TrafficLightStrategy {

    private final TrafficLightJunction trafficLightJunction;

    public RoundRobinTimerTrafficLightStrategy(TrafficLightJunction trafficLightJunction) {
        this.trafficLightJunction = trafficLightJunction;
    }

    @Override
    public void updateTrafficLights() {
    }

}

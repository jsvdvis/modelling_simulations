package nl.rug.modellingsimulations.model.trafficlightstrategy;

import nl.rug.modellingsimulations.model.TrafficLightJunction;

public class RoundRobinTimerTrafficLightStrategy implements TrafficLightStrategy {

    private static RoundRobinTimerTrafficLightStrategy instance = null;

    private RoundRobinTimerTrafficLightStrategy() {}

    public static RoundRobinTimerTrafficLightStrategy getInstance() {
        if (instance == null) {
            instance = new RoundRobinTimerTrafficLightStrategy();
        }
        return instance;
    }

    @Override
    public void updateTrafficLights(TrafficLightJunction trafficLightJunction) {

    }

}

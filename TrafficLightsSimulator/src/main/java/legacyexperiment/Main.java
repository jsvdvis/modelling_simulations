package legacyexperiment;

import legacyexperiment.simulations.SimpleWorld;
import legacyexperiment.simulator.Simulator;

public class Main {
    public static void main(String[] args) {
        Simulator simulator = new Simulator(new SimpleWorld());
        simulator.run();
    }
}

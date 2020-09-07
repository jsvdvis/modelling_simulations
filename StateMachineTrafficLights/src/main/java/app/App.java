package app;

import app.world.SimpleWorld;
import app.world.SmallCircleWorld;

public class App {
    public static void main(String[] args) {
        Simulation simulation = new Simulation(new SimpleWorld());
        simulation.run();
    }
}

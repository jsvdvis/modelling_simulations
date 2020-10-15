package nl.rug.modellingsimulations;

import nl.rug.modellingsimulations.simulation.CityGrid;
import nl.rug.modellingsimulations.simulation.Simulation;

public class Main {

    public static void main(String[] args) {
        System.setProperty("org.graphstream.ui", "swing");
        Simulation simulation = new CityGrid(20);
        Simulator simulator = new Simulator(simulation);
        simulator.run();
    }

}

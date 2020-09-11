package nl.rug.modellingsimulations;

import nl.rug.modellingsimulations.simulation.SimpleWorld;

public class Main {

    public static void main(String[] args) {
        Simulator simulator = new Simulator();
        simulator.run(new SimpleWorld());
    }

}

package nl.rug.modellingsimulations;

import nl.rug.modellingsimulations.simulation.SimpleWorld;
import nl.rug.modellingsimulations.utilities.Point;

public class Main {

    public static void main(String[] args) {
        System.setProperty("org.graphstream.ui", "swing");

        Point center = new Point(0, 0);
        Point point = new Point(10, 0);
        Point rotated = point.rotate_around(center, 90);
        System.out.println("x: " + rotated.getX() + " y: " + rotated.getY());

        Simulator simulator = new Simulator();
        simulator.run(new SimpleWorld());
    }

}

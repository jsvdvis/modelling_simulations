package nl.rug.modellingsimulations.simulation;

import nl.rug.modellingsimulations.model.TrafficLightJunction;
import nl.rug.modellingsimulations.model.navigablenode.VehicleSourceNavigableNode;
import nl.rug.modellingsimulations.model.vehicle.Vehicle;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

import java.util.List;

public class SimpleWorld implements Simulation {

    public SimpleWorld() {
        Graph graph = new SingleGraph("SimpleWorld");
        graph.addNode("aa");
        graph.display();
        System.exit(0);
    }

    @Override
    public List<TrafficLightJunction> getTrafficLightJunctions() {
        return null;
    }

    @Override
    public List<Vehicle> getVehicles() {
        return null;
    }

    @Override
    public List<VehicleSourceNavigableNode> getSources() {
        return null;
    }
}

package nl.rug.modellingsimulations.simulation;

import nl.rug.modellingsimulations.Simulator;
import nl.rug.modellingsimulations.config.SimulationConfig;
import nl.rug.modellingsimulations.model.TrafficLightJunction;
import nl.rug.modellingsimulations.model.navigablenode.VehicleSourceNavigableNode;
import nl.rug.modellingsimulations.model.vehicle.Vehicle;
import org.graphstream.graph.Graph;

import java.util.List;

public interface Simulation {

    List<TrafficLightJunction> getTrafficLightJunctions();
    List<Vehicle> getVehicles();
    List<VehicleSourceNavigableNode> getSources();
    SimulationConfig getConfig();

}

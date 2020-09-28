package nl.rug.modellingsimulations.simulation;

import nl.rug.modellingsimulations.config.SimulationConfig;
import nl.rug.modellingsimulations.model.trafficlight.TrafficLightJunction;
import nl.rug.modellingsimulations.model.navigablenode.VehicleSourceNavigableNode;
import nl.rug.modellingsimulations.model.vehicle.Vehicle;

import java.util.List;

public interface Simulation {

    List<TrafficLightJunction> getTrafficLightJunctions();
    List<Vehicle> getVehicles();
    List<VehicleSourceNavigableNode> getSources();
    SimulationConfig getConfig();

    void addNewVehicle(Vehicle vehicle);
    void removeVehicle(Vehicle vehicle);
}

package nl.rug.modellingsimulations.simulation;

import nl.rug.modellingsimulations.model.TrafficLightJunction;
import nl.rug.modellingsimulations.model.navigablenode.*;
import nl.rug.modellingsimulations.model.trafficlightstrategy.RoundRobinTimerTrafficLightStrategy;
import nl.rug.modellingsimulations.model.vehicle.Vehicle;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

import java.util.ArrayList;
import java.util.List;

public class SimpleWorld implements Simulation {

    private ArrayList<NavigableNode> nodes = new ArrayList<>();
    private ArrayList<TrafficLightJunction> junctions = new ArrayList<>();
    private ArrayList<Vehicle> vehicles = new ArrayList<>();
    private ArrayList<VehicleSourceNavigableNode> sources = new ArrayList<>();

    public SimpleWorld() {
        initializeWorld();
    }

    private void initializeWorld() {
        this.addNavigableNode(new VehicleSourceNavigableNode());
        this.addNavigableNode(new VehicleSinkNavigableNode());

        TrafficLightJunction junction = new TrafficLightJunction(RoundRobinTimerTrafficLightStrategy.getInstance());
        JunctionLaneNavigableNode lane1 = new JunctionLaneNavigableNode(3);
        JunctionExitNavigableNode exit1 = new JunctionExitNavigableNode();
        junction.addLane(lane1, exit1);
    }

    @Override
    public List<TrafficLightJunction> getTrafficLightJunctions() {
        return junctions;
    }

    @Override
    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    @Override
    public List<VehicleSourceNavigableNode> getSources() {
        return sources;
    }

    public void addNavigableNode(NavigableNode node) {
        this.nodes.add(node);
    }

}

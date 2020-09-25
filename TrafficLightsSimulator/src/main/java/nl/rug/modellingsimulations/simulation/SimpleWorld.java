package nl.rug.modellingsimulations.simulation;

import nl.rug.modellingsimulations.config.DefaultSimulationConfig;
import nl.rug.modellingsimulations.config.SimulationConfig;
import nl.rug.modellingsimulations.graph.GraphMediator;
import nl.rug.modellingsimulations.graph.GraphStreamMediator;
import nl.rug.modellingsimulations.model.SimpleTrafficLightJunction;
import nl.rug.modellingsimulations.model.TrafficLightJunction;
import nl.rug.modellingsimulations.model.navigablenode.*;
import nl.rug.modellingsimulations.model.trafficlightstrategy.RoundRobinTimerTrafficLightStrategy;
import nl.rug.modellingsimulations.model.trafficlightstrategy.TrafficLightStrategy;
import nl.rug.modellingsimulations.model.vehicle.Vehicle;
import nl.rug.modellingsimulations.utilities.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleWorld implements Simulation {

    private List<NavigableNode> nodes;
    private List<TrafficLightJunction> junctions;
    private ArrayList<Vehicle> vehicles = new ArrayList<>();
    private SimulationConfig simulationConfig = DefaultSimulationConfig.getInstance();

    public SimpleWorld() {
        initializeWorld();
    }

    private void initializeWorld() {
        SimulationBuilder builder = new SimulationBuilder(this);
        VehicleSourceNavigableNode vehicleSourceNavigableNode = new VehicleSourceNavigableNode(new Point(0, 5));
        VehicleSinkNavigableNode vehicleSinkNavigableNode1 = new VehicleSinkNavigableNode(new Point(0,-1));
        VehicleSinkNavigableNode vehicleSinkNavigableNode2 = new VehicleSinkNavigableNode(new Point(4,5));

        TrafficLightJunction junction1 = new SimpleTrafficLightJunction(new Point(0, 0));
        junction1.setTrafficLightStrategy(new RoundRobinTimerTrafficLightStrategy(junction1));
        TrafficLightJunction junction2 = new SimpleTrafficLightJunction(new Point(0, 4));
        junction2.setTrafficLightStrategy(new RoundRobinTimerTrafficLightStrategy(junction2));
        TrafficLightJunction junction3 = new SimpleTrafficLightJunction(new Point(4, 4));
        junction3.setTrafficLightStrategy(new RoundRobinTimerTrafficLightStrategy(junction3));
        TrafficLightJunction junction4 = new SimpleTrafficLightJunction(new Point(4, 0));
        junction4.setTrafficLightStrategy(new RoundRobinTimerTrafficLightStrategy(junction4));

//        builder.connectTwoWayJunction(junction1, junction2);
//        builder.connect(junction2, junction3);
//        builder.connect(junction3, junction1);
//        builder.connect(junction1, junction3);

//        builder.connect(vehicleSourceNavigableNode, junction2);
//        builder.connect(junction1, vehicleSinkNavigableNode1);
//        builder.connect(junction3, vehicleSinkNavigableNode2);

        builder.connectTwoWayJunction(junction1, junction2);
        builder.connectTwoWayJunction(junction1, junction4);
        builder.connectTwoWayJunction(junction2, junction3);
        builder.connectTwoWayJunction(junction3, junction4);

        builder.connect(vehicleSourceNavigableNode, junction2);
        builder.connect(junction1, vehicleSinkNavigableNode1);
        builder.connect(junction3, vehicleSinkNavigableNode2);

        builder.build();
        builder.buildExemptedLanes();

        this.junctions = builder.getJunctions();
        this.nodes = builder.getNavigableNodes();
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
        return nodes.parallelStream()
                .filter(x -> x instanceof VehicleSourceNavigableNode)
                .map(x -> (VehicleSourceNavigableNode) x)
                .collect(Collectors.toList());
    }

    public SimulationConfig getConfig() {
        return this.simulationConfig;
    }

    @Override
    public void addNewVehicle(Vehicle vehicle) {
        if(!vehicle.getCurrentNavigableNode().canMovePosition(vehicle))
            throw new IllegalStateException("Trying to add vehicle to source while it is full!");

        vehicle.getCurrentNavigableNode().movePosition(vehicle);
        this.vehicles.add(vehicle);
    }

    @Override
    public void removeVehicle(Vehicle vehicle) {
        this.vehicles.remove(vehicle);
    }


}

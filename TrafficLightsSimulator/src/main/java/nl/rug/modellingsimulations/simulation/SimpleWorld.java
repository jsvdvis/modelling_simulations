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
    private GraphMediator graphMediator;

    public SimpleWorld() {
        initializeWorld();
        initializeGraphMediator();
    }

    private void initializeGraphMediator() {
        this.graphMediator = new GraphStreamMediator(this);
        this.graphMediator.createGraph();
    }

    private void initializeWorld() {
        SimulationBuilder builder = new SimulationBuilder(this);
        VehicleSourceNavigableNode vehicleSourceNavigableNode = new VehicleSourceNavigableNode(new Point(0, 5));
        VehicleSinkNavigableNode vehicleSinkNavigableNode1 = new VehicleSinkNavigableNode(new Point(1,0));
        VehicleSinkNavigableNode vehicleSinkNavigableNode2 = new VehicleSinkNavigableNode(new Point(5,3));

        TrafficLightStrategy trafficLightStrategy = RoundRobinTimerTrafficLightStrategy.getInstance();

        TrafficLightJunction junction1 = new SimpleTrafficLightJunction(trafficLightStrategy, new Point(0, 0));
        TrafficLightJunction junction2 = new SimpleTrafficLightJunction(trafficLightStrategy, new Point(0, 4));
        TrafficLightJunction junction3 = new SimpleTrafficLightJunction(trafficLightStrategy, new Point(5, 4));

        builder.connectTwoWayJunction(junction1, junction2);
        builder.connect(junction2, junction3);
        builder.connect(junction3, junction1);

        builder.connect(vehicleSourceNavigableNode, junction2);
        builder.connect(junction1, vehicleSinkNavigableNode1);
        builder.connect(junction3, vehicleSinkNavigableNode2);
        builder.build();

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

}

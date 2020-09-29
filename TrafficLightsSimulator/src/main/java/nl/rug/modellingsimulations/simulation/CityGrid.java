package nl.rug.modellingsimulations.simulation;

import nl.rug.modellingsimulations.config.DefaultSimulationConfig;
import nl.rug.modellingsimulations.config.SimulationConfig;
import nl.rug.modellingsimulations.model.navigablenode.NavigableNode;
import nl.rug.modellingsimulations.model.navigablenode.VehicleSinkNavigableNode;
import nl.rug.modellingsimulations.model.navigablenode.VehicleSourceNavigableNode;
import nl.rug.modellingsimulations.model.trafficlight.SimpleTrafficLightJunction;
import nl.rug.modellingsimulations.model.trafficlight.TrafficLightJunction;
import nl.rug.modellingsimulations.model.trafficlight.trafficlightstrategy.RoundRobinTimerTrafficLightStrategy;
import nl.rug.modellingsimulations.model.vehicle.Vehicle;
import nl.rug.modellingsimulations.utilities.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CityGrid implements Simulation {

    private List<NavigableNode> nodes;
    private List<TrafficLightJunction> junctions;
    private ArrayList<Vehicle> vehicles = new ArrayList<>();
    private SimulationConfig simulationConfig = DefaultSimulationConfig.getInstance();

    public CityGrid(int N) {
        initializeWorld(N);
    }

    private void initializeWorld(int N) {
        SimulationBuilder builder = new SimulationBuilder(this);
        List<List<TrafficLightJunction>> junctionGrid = new ArrayList<>();
        for (int i = 0; i < N; i += 1) {
            List<TrafficLightJunction> junctionRow = new ArrayList<>();

            for (int j = 0; j < N; j += 1) {
                TrafficLightJunction junction = new SimpleTrafficLightJunction(new Point(j * 4, i * 4));
                junction.setTrafficLightStrategy(new RoundRobinTimerTrafficLightStrategy(junction));

                junctionRow.add(junction);
                if (j > 0) {
                    builder.connectTwoWayJunction(junctionRow.get(j), junctionRow.get(j - 1));
                }
            }

            junctionGrid.add(junctionRow);
            if (i > 0) {
                List<TrafficLightJunction> previousJunctionRow = junctionGrid.get(i - 1);
                for (int j = 0; j < N; j += 1) {
                    builder.connectTwoWayJunction(junctionRow.get(j), previousJunctionRow.get(j));
                }
            }
        }

        for (int i = 0; i < N; i += 1) {
            TrafficLightJunction bottomRowJunction = junctionGrid.get(0).get(i);
            VehicleSinkNavigableNode bottomRowSink = new VehicleSinkNavigableNode(new Point(i * 4 - 1, -4));
            VehicleSourceNavigableNode bottomRowSource = new VehicleSourceNavigableNode(new Point(i * 4 + 1, -4));
            builder.connect(bottomRowJunction, bottomRowSink);
            builder.connect(bottomRowSource, bottomRowJunction);

            TrafficLightJunction topRowJunction = junctionGrid.get(N - 1).get(i);
            VehicleSinkNavigableNode topRowSink = new VehicleSinkNavigableNode(new Point(i * 4 - 1, N * 4));
            VehicleSourceNavigableNode topRowSource = new VehicleSourceNavigableNode(new Point(i * 4 + 1, N * 4));
            builder.connect(topRowJunction, topRowSink);
            builder.connect(topRowSource, topRowJunction);

            TrafficLightJunction leftColumnJunction = junctionGrid.get(i).get(0);
            VehicleSinkNavigableNode leftColumnSink = new VehicleSinkNavigableNode(new Point(-4, i * 4 - 1));
            VehicleSourceNavigableNode leftColumnSource = new VehicleSourceNavigableNode(new Point(-4, i * 4 + 1));
            builder.connect(leftColumnJunction, leftColumnSink);
            builder.connect(leftColumnSource, leftColumnJunction);
//
            TrafficLightJunction rightColumnJunction = junctionGrid.get(i).get(N - 1);
            VehicleSinkNavigableNode rightColumnSink = new VehicleSinkNavigableNode(new Point(N * 4, i * 4 - 1));
            VehicleSourceNavigableNode rightColumnSource = new VehicleSourceNavigableNode(new Point(N * 4, i * 4 + 1));
            builder.connect(rightColumnJunction, rightColumnSink);
            builder.connect(rightColumnSource, rightColumnJunction);
        }

//        VehicleSourceNavigableNode vehicleSourceNavigableNode = new VehicleSourceNavigableNode(new Point(0, 5));
//        VehicleSinkNavigableNode vehicleSinkNavigableNode1 = new VehicleSinkNavigableNode(new Point(0,-1));
//        VehicleSinkNavigableNode vehicleSinkNavigableNode2 = new VehicleSinkNavigableNode(new Point(4,5));
//
//        TrafficLightJunction junction1 = new SimpleTrafficLightJunction(new Point(0, 0));
//        junction1.setTrafficLightStrategy(new RoundRobinTimerTrafficLightStrategy(junction1));
//        TrafficLightJunction junction2 = new SimpleTrafficLightJunction(new Point(0, 4));
//        junction2.setTrafficLightStrategy(new RoundRobinTimerTrafficLightStrategy(junction2));
//        TrafficLightJunction junction3 = new SimpleTrafficLightJunction(new Point(4, 4));
//        junction3.setTrafficLightStrategy(new RoundRobinTimerTrafficLightStrategy(junction3));
//        TrafficLightJunction junction4 = new SimpleTrafficLightJunction(new Point(4, 0));
//        junction4.setTrafficLightStrategy(new RoundRobinTimerTrafficLightStrategy(junction4));

//        builder.connectTwoWayJunction(junction1, junction2);
//        builder.connect(junction2, junction3);
//        builder.connect(junction3, junction1);
//        builder.connect(junction1, junction3);

//        builder.connect(vehicleSourceNavigableNode, junction2);
//        builder.connect(junction1, vehicleSinkNavigableNode1);
//        builder.connect(junction3, vehicleSinkNavigableNode2);

//        builder.connectTwoWayJunction(junction1, junction2);
//        builder.connectTwoWayJunction(junction1, junction4);
//        builder.connectTwoWayJunction(junction2, junction3);
//        builder.connectTwoWayJunction(junction3, junction4);
//
//        builder.connect(vehicleSourceNavigableNode, junction2);
//        builder.connect(junction1, vehicleSinkNavigableNode1);
//        builder.connect(junction3, vehicleSinkNavigableNode2);

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

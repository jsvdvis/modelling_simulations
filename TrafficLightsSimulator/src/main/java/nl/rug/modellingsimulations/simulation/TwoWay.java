package nl.rug.modellingsimulations.simulation;

import nl.rug.modellingsimulations.Simulator;
import nl.rug.modellingsimulations.config.CityGridConfig;
import nl.rug.modellingsimulations.config.DefaultSimulationConfig;
import nl.rug.modellingsimulations.config.SimulationConfig;
import nl.rug.modellingsimulations.model.navigablenode.NavigableNode;
import nl.rug.modellingsimulations.model.navigablenode.VehicleSinkNavigableNode;
import nl.rug.modellingsimulations.model.navigablenode.VehicleSourceNavigableNode;
import nl.rug.modellingsimulations.model.trafficlight.SimpleTrafficLightJunction;
import nl.rug.modellingsimulations.model.trafficlight.TrafficLightJunction;
import nl.rug.modellingsimulations.model.trafficlight.trafficlightstrategy.SensoredFifoTrafficLightStrategy;
import nl.rug.modellingsimulations.model.vehicle.Vehicle;
import nl.rug.modellingsimulations.utilities.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TwoWay implements Simulation {

    private List<NavigableNode> nodes;
    private List<TrafficLightJunction> junctions;
    private ArrayList<Vehicle> vehicles = new ArrayList<>();
    private SimulationConfig simulationConfig = DefaultSimulationConfig.getInstance();

    public TwoWay(int N) {
        initializeWorld(N);
    }

    private void initializeWorld(int N) {
        int spacing = CityGridConfig.getSpacingBetweenJunctions();

        SimulationBuilder builder = new SimulationBuilder(this);
        List<TrafficLightJunction> junctions = new ArrayList<>();

        for (int i = 0; i < N; i += 1) {
            TrafficLightJunction junction = new SimpleTrafficLightJunction(
                    new Point(i * spacing, 0)
            );
            junction.setTrafficLightStrategy(Simulator.createRandomTrafficLightStrategy(junction));
            junctions.add(junction);

            if (i > 0) {
                builder.connectTwoWayJunction(junction, junctions.get(i - 1));
            }
        }
        VehicleSourceNavigableNode source1 = new VehicleSourceNavigableNode(
                new Point(-spacing, -1)
        );
        VehicleSinkNavigableNode sink1 = new VehicleSinkNavigableNode(
                new Point(-spacing, 1)
        );
        builder.connect(source1, junctions.get(0));
        builder.connect(junctions.get(0), sink1);

        VehicleSourceNavigableNode source2 = new VehicleSourceNavigableNode(
                new Point(N * spacing, 1)
        );
        VehicleSinkNavigableNode sink2 = new VehicleSinkNavigableNode(
                new Point(N * spacing, -1)
        );
        builder.connect(source2, junctions.get(N - 1));
        builder.connect(junctions.get(N - 1), sink2);


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

    @Override
    public List<NavigableNode> getNodes() {
        return nodes;
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

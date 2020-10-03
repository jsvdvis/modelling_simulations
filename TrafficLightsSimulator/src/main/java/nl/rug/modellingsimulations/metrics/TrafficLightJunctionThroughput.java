package nl.rug.modellingsimulations.metrics;

import nl.rug.modellingsimulations.simulation.Simulation;

public class TrafficLightJunctionThroughput extends SimulationMetricsMeasurer {
    private int vehicleThroughput;

    public TrafficLightJunctionThroughput(MetricsStepResultSaver saver) {
        super(saver);
        this.addDataToSaver("Absolute Throughput");
        this.addDataToSaver("Total Number of Vehicles");
        this.addDataToSaver("Relative Throughput");
        super.finishSimulationStep(null);
    }

    @Override
    public void initSimulationStep() {
        vehicleThroughput = 0;
    }

    @Override
    public void finishSimulationStep(Simulation simulation) {
        int numberOfVehicles = simulation.getVehicles().size();
        this.addDataToSaver(String.valueOf(vehicleThroughput));
        this.addDataToSaver(String.valueOf(numberOfVehicles));
        this.addDataToSaver(String.valueOf(((float)vehicleThroughput) / numberOfVehicles));
        super.finishSimulationStep(simulation);
    }

    public void increaseThroughput() {
        vehicleThroughput += 1;
    }
}

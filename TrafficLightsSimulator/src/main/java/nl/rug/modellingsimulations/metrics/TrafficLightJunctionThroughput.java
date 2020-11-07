package nl.rug.modellingsimulations.metrics;

import nl.rug.modellingsimulations.simulation.Simulation;

public class TrafficLightJunctionThroughput extends SimulationMetricsMeasurer {
    protected long vehicleThroughput;

    public TrafficLightJunctionThroughput(MetricsStepResultSaver saver) {
        super(saver);
        this.addDataToSaver("Absolute Throughput (Left Axis)");
        this.addDataToSaver("Total Number of Vehicles (Right Axis)");
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
        this.addDataToSaver(String.valueOf(((double)vehicleThroughput) / numberOfVehicles));
        super.finishSimulationStep(simulation);
    }

    public void increaseThroughput() {
        vehicleThroughput += 1;
    }
}

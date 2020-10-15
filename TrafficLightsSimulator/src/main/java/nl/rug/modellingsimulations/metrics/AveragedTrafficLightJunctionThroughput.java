package nl.rug.modellingsimulations.metrics;

import nl.rug.modellingsimulations.simulation.Simulation;

public class AveragedTrafficLightJunctionThroughput extends SimulationMetricsMeasurer {
    private final int STEPS_PER_AVERAGE = 100;
    private int stepCounter = 0;
    private long vehicleThroughput = 0;

    public AveragedTrafficLightJunctionThroughput(MetricsStepResultSaver saver) {
        super(saver);
        this.addDataToSaver("Absolute Throughput");
        this.addDataToSaver("Total Number of Vehicles");
        this.addDataToSaver("Relative Throughput");
        super.finishSimulationStep(null);
    }

    @Override
    public void initSimulationStep() {
        // Do nothing
    }

    @Override
    public void finishSimulationStep(Simulation simulation) {
        stepCounter += 1;

        if (stepCounter >= STEPS_PER_AVERAGE) {
            int numberOfVehicles = simulation.getVehicles().size();
            double averageVehicleThroughput = ((double) vehicleThroughput) / STEPS_PER_AVERAGE;
            this.addDataToSaver(String.valueOf(averageVehicleThroughput));
            this.addDataToSaver(String.valueOf(numberOfVehicles));
            this.addDataToSaver(String.valueOf(averageVehicleThroughput / numberOfVehicles));
            super.finishSimulationStep(simulation);

            stepCounter = 0;
            vehicleThroughput = 0;
        }
    }

    public void increaseThroughput() {
        vehicleThroughput += 1;
    }
}

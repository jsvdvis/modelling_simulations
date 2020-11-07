package nl.rug.modellingsimulations.metrics;

import nl.rug.modellingsimulations.simulation.Simulation;

public class AveragedStoppedVehicles extends SimulationMetricsMeasurer {
    private final int STEPS_PER_AVERAGE = 100;
    private int stepCounter = 0;
    private long stoppedVehicleCount = 0;

    public AveragedStoppedVehicles(MetricsStepResultSaver saver) {
        super(saver);
        this.addDataToSaver("Stopped vehicles");
        this.addDataToSaver("Total Number of Vehicles");
        this.addDataToSaver("Relative stopped vehicles");
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
            double averageStoppedVehicles = ((double) stoppedVehicleCount) / STEPS_PER_AVERAGE;
            this.addDataToSaver(String.valueOf(averageStoppedVehicles));
            this.addDataToSaver(String.valueOf(numberOfVehicles));
            this.addDataToSaver(String.valueOf(averageStoppedVehicles / numberOfVehicles));
            super.finishSimulationStep(simulation);

            stepCounter = 0;
            stoppedVehicleCount = 0;
        }
    }

    public void addStoppedVehicleCount(int count) {
        stoppedVehicleCount += count;
    }
}

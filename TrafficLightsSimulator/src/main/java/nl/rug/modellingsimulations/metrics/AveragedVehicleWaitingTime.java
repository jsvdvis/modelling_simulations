package nl.rug.modellingsimulations.metrics;

import nl.rug.modellingsimulations.simulation.Simulation;

public class AveragedVehicleWaitingTime extends SimulationMetricsMeasurer {
    private final int STEPS_PER_AVERAGE = 100;
    private int stepCounter = 0;
    private long vehicleWaitingTime = 0;
    private long maxWaitingTimeForStep = 0;

    public AveragedVehicleWaitingTime(MetricsStepResultSaver saver) {
        super(saver);
        this.addDataToSaver("Vehicle waiting time");
        this.addDataToSaver("Total Number of Vehicles");
        this.addDataToSaver("Relative waiting time vehicles");
        this.addDataToSaver("Maximum Vehicle Waiting Time");
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
            double averageWaitingTime = ((double)vehicleWaitingTime) / STEPS_PER_AVERAGE;
            this.addDataToSaver(String.valueOf(averageWaitingTime));
            this.addDataToSaver(String.valueOf(numberOfVehicles));
            this.addDataToSaver(String.valueOf(averageWaitingTime / numberOfVehicles));
            this.addDataToSaver(String.valueOf(maxWaitingTimeForStep));
            super.finishSimulationStep(simulation);

            stepCounter = 0;
            vehicleWaitingTime = 0;
            maxWaitingTimeForStep = 0;
        }
    }

    public void addVehicleWaitingTime(int waitingTime) {
        this.vehicleWaitingTime += waitingTime;
        if(waitingTime > maxWaitingTimeForStep)
            maxWaitingTimeForStep = waitingTime;
    }
}

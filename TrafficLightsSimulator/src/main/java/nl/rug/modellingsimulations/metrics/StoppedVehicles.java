package nl.rug.modellingsimulations.metrics;

import nl.rug.modellingsimulations.simulation.Simulation;

public class StoppedVehicles extends SimulationMetricsMeasurer {
    private int stoppedVehicleCount;

    public StoppedVehicles(MetricsStepResultSaver saver) {
        super(saver);
        this.addDataToSaver("Stopped vehicles");
        this.addDataToSaver("Total Number of Vehicles");
        this.addDataToSaver("Relative stopped vehicles");
        super.finishSimulationStep(null);
    }

    @Override
    public void initSimulationStep() {
        stoppedVehicleCount = 0;
    }

    @Override
    public void finishSimulationStep(Simulation simulation) {
        int numberOfVehicles = simulation.getVehicles().size();
        this.addDataToSaver(String.valueOf(stoppedVehicleCount));
        this.addDataToSaver(String.valueOf(numberOfVehicles));
        this.addDataToSaver(String.valueOf(((float)stoppedVehicleCount) / numberOfVehicles));
        super.finishSimulationStep(simulation);
    }

    public void increaseStoppedVehicleCount() {
        stoppedVehicleCount += 1;
    }

    public void setStoppedVehicleCount(int count) {
        stoppedVehicleCount = count;
    }
}

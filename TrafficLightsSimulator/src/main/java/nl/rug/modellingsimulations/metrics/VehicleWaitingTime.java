package nl.rug.modellingsimulations.metrics;

import nl.rug.modellingsimulations.simulation.Simulation;

public class VehicleWaitingTime extends SimulationMetricsMeasurer {
    protected long vehicleWaitingTime;

    public VehicleWaitingTime(MetricsStepResultSaver saver) {
        super(saver);
        this.addDataToSaver("Vehicle waiting time (Left Axis)");
        this.addDataToSaver("Total Number of Vehicles (Right Axis)");
        this.addDataToSaver("Relative waiting time vehicles");
        super.finishSimulationStep(null);
    }

    @Override
    public void initSimulationStep() {
        vehicleWaitingTime = 0;
    }

    @Override
    public void finishSimulationStep(Simulation simulation) {
        int numberOfVehicles = simulation.getVehicles().size();
        this.addDataToSaver(String.valueOf(vehicleWaitingTime));
        this.addDataToSaver(String.valueOf(numberOfVehicles));
        this.addDataToSaver(String.valueOf(((double) vehicleWaitingTime) / numberOfVehicles));
        super.finishSimulationStep(simulation);
    }

    public void addVehicleWaitingTime(int waitingTime) {
        this.vehicleWaitingTime += waitingTime;
    }
}

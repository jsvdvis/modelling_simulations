package nl.rug.modellingsimulations.metrics;

import nl.rug.modellingsimulations.simulation.Simulation;

public abstract class SimulationMetricsMeasurer {
    private MetricsStepResultSaver saver;

    public SimulationMetricsMeasurer(MetricsStepResultSaver saver) {
        this.saver = saver;
    }

    public void finishSimulationStep(Simulation simulation) {
        this.saver.save();
        initSimulationStep();
    }

    protected void addDataToSaver(String data) {
        this.saver.addData(data);
    };
    public abstract void initSimulationStep();
}

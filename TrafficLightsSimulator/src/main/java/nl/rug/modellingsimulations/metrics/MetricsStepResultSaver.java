package nl.rug.modellingsimulations.metrics;

public interface MetricsStepResultSaver {
    void addData(String data);
    void save();
}

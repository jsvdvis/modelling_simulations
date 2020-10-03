package nl.rug.modellingsimulations.metrics;

public class SystemOutPrintSaver implements MetricsStepResultSaver {
    private String data = "";

    @Override
    public void addData(String data) {
        this.data = this.data.equals("") ? data : this.data + "," + data;
    }

    @Override
    public void save() {
        System.out.println(data);
        data = "";
    }
}

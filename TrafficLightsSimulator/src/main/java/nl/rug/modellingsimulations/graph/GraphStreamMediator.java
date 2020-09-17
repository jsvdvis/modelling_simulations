package nl.rug.modellingsimulations.graph;

import nl.rug.modellingsimulations.simulation.Simulation;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

public class GraphStreamMediator implements GraphMediator {

    private Simulation simulation;
    private Graph graph;

    public GraphStreamMediator(Simulation simulation) {
        this.simulation = simulation;
    }

    public void initializeGraph() {
        this.graph = new SingleGraph("simulation");
        graph.display();
        this.initializeNodes();
        this.initializeEdges();
    }

    private void initializeNodes() {

    }

    private void initializeEdges() {
    }

    public void updateView(Simulation simulation) {

    }
}

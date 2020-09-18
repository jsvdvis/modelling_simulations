package nl.rug.modellingsimulations.graph;

import nl.rug.modellingsimulations.model.navigablenode.*;
import nl.rug.modellingsimulations.simulation.Simulation;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swing.util.ImageCache;
import org.graphstream.ui.view.Viewer;

import java.util.*;
import java.util.stream.Collectors;

public class GraphStreamMediator implements GraphMediator {

    private final Map<NavigableNode, Node> nodes = new HashMap<>();
    private Simulation simulation;
    private Graph graph;
    private Viewer viewer;

    public GraphStreamMediator(Simulation simulation) {
        this.simulation = simulation;
    }

    public void createGraph() {
        this.initializeGraph();
        this.createNodesAndEdges(
                (List<NavigableNode>)(List<?>) simulation.getSources(),
                new ArrayList<>()
        );
    }

    public void updateView() {
        nodes.keySet().forEach(this::updateNodeAttributes);
    }

    private void initializeGraph() {
        graph = new SingleGraph("simulation");
        graph.setAttribute(
                "ui.stylesheet",
                "node.road { " +
                        "   fill-color: black; " +
                        "}" +
                        " " +
                        "node.source { " +
                        "   fill-color: lightgreen; " +
                        "   stroke-mode: plain; " +
                        "   stroke-color: #999; " +
                        "   size: 35px;"+
                        "   shadow-mode: plain; " +
                        "   shadow-color: green; " +
                        "   shadow-width: 4px; " +
                        "   shadow-offset: 0px; " +
                        "   icon-mode: at-right;"+
                        "   icon: dyn-icon;"+
                        "}" +
                        " " +
                        "node.sink { " +
                        "   fill-color: pink; " +
                        "   stroke-mode: plain; " +
                        "   stroke-color: #999; " +
                        "   size: 35px;"+
                        "   shadow-mode: plain; " +
                        "   shadow-color: red; " +
                        "   shadow-width: 4px; " +
                        "   shadow-offset: 0px; " +
                        "   icon-mode: at-left;"+
                        "   icon: dyn-icon;"+
                        "}" +
                        " " +
                        "node.lane { " +
                        "   fill-color: purple; " +
                        "}" +
                        " " +
                        "node.exit { " +
                        "   fill-color: blue; " +
                        "}" +
                        " " +
                        "edge {" +
                        "   shadow-mode: plain; " +
                        "   shadow-width: 3px; " +
                        "   shadow-color: orange; " +
                        "   shadow-offset: 0px; " +
                        "   arrow-shape: arrow; " +
                        "   arrow-size: 5px, 5px; " +
                        //"   shape: angle; " +
                        //"   size: 5px; " +
                        "}"
        );
        graph.setAttribute("ui.antialias");
        graph.setAttribute("ui.quality");
        viewer = graph.display();
        viewer.disableAutoLayout();
    }

    private void createNodesAndEdges(List<NavigableNode> nodesToProcess, List<NavigableNode> processedNodes) {
        // Base case
        if(nodesToProcess.size() == 0)
            return;

        nodesToProcess = nodesToProcess.stream()
                // Add the current nodes to the graph
                .peek(this::createGraphNode)
                // Add all the next nodes to the graph
                .peek(currentNode -> currentNode.getNextNodes().forEach(this::createGraphNode))
                // Add the edges between the current nodes and the next nodes
                .peek(currentNode ->
                    currentNode.getNextNodes().forEach(nextNode -> createGraphEdge(currentNode, nextNode)))
                // Mark the current node as being finished with processing
                .peek(processedNodes::add)
                // Map to the next nodes, since we want to process those next
                .flatMap(currentNode -> currentNode.getNextNodes().stream())
                // Filter the nodes to those that have not been processed yet
                .filter(nextNode -> !processedNodes.contains(nextNode))
                // Collect them in a list
                .collect(Collectors.toList());

        // Recursively process the next nodes
        this.createNodesAndEdges(nodesToProcess, processedNodes);
    }

    private void createGraphNode(NavigableNode navigableNode) {
        if (!nodes.containsKey(navigableNode)) {
            String nodeId = "node_" + nodes.size();
            Node node = graph.addNode(nodeId);
            node.setAttribute("x", navigableNode.getPosition(true).getX());
            node.setAttribute("y", navigableNode.getPosition(true).getY());
            nodes.put(navigableNode, node);
            updateNodeAttributes(navigableNode);
        }
    }

    private void updateNodeAttributes(NavigableNode navigableNode) {
        if(!this.nodes.containsKey(navigableNode))
            throw new IllegalStateException("Trying to update node's attributes that is not in graph.");

        Node node = this.nodes.get(navigableNode);
        if (navigableNode instanceof RoadNavigableNode) {
            node.setAttribute("ui.class", "road");
        } else if (navigableNode instanceof JunctionLaneNavigableNode) {
            node.setAttribute("ui.class", "lane");
        } else if (navigableNode instanceof JunctionExitNavigableNode) {
            node.setAttribute("ui.class", "exit");
        } else if (navigableNode instanceof VehicleSourceNavigableNode) {
            node.setAttribute("ui.class", "source");
            node.setAttribute("ui.icon", ImageCache.class.getClassLoader().getResource("source.png").toString());
        } else if (navigableNode instanceof VehicleSinkNavigableNode) {
            node.setAttribute("ui.class", "sink");
            node.setAttribute("ui.icon", ImageCache.class.getClassLoader().getResource("sink.png").toString());
        }
    }

    private void createGraphEdge(NavigableNode from, NavigableNode to) {
        if(!nodes.containsKey(from) || !nodes.containsKey(to))
            throw new IllegalStateException("One of the nodes to add an edge does not exist in the graph yet!");
        String fromNodeId = nodes.get(from).getId();
        String toNodeId = nodes.get(to).getId();
        String edgeId = fromNodeId + "_" + toNodeId;
        if(graph.getEdge(edgeId) != null)
            return; // Already exists
        graph.addEdge(edgeId, fromNodeId, toNodeId, true);
    }
}

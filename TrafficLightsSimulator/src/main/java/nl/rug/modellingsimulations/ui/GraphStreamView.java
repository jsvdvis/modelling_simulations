package nl.rug.modellingsimulations.ui;

import nl.rug.modellingsimulations.model.navigablenode.*;
import nl.rug.modellingsimulations.simulation.Simulation;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GraphStreamView {

    private final Map<NavigableNode, String> nodes = new HashMap<>();

    public GraphStreamView () {}

    public void updateView(Simulation simulation) {
        renderSimulation(simulation);
    }

    private void renderSimulation(Simulation simulation) {
        Set<NavigableNode> renderedNodes = new HashSet<>();
        Set<NavigableNode> nodesToRender = new HashSet<>(simulation.getSources());

        Graph graph = new SingleGraph("Simple graph");
        graph.setAttribute(
                "ui.stylesheet",
                "node.road { " +
                        "   fill-color: black; " +
                        "}" +
                        " " +
                        "node.source { " +
                        "   fill-color: green; " +
                        "}" +
                        " " +
                        "node.sink { " +
                        "   fill-color: red; " +
                        "}" +
                        " " +
                        "node.lane { " +
                        "   fill-color: purple; " +
                        "}" +
                        " " +
                        "node.exit { " +
                        "   fill-color: blue; " +
                        "}"
        );

        while (!nodesToRender.isEmpty()) {
            renderedNodes.addAll(nodesToRender);
            nodesToRender = nodesToRender
                    .stream()
                    .flatMap(navigableNode -> {
                        if (!nodes.containsKey(navigableNode)) {
                            String nodeId = "node_" + nodes.size();
                            Node node = graph.addNode(nodeId);
                            nodes.put(navigableNode, nodeId);

                            if (navigableNode instanceof RoadNavigableNode) {
                                node.setAttribute("ui.class", "road");
                            } else if (navigableNode instanceof JunctionLaneNavigableNode) {
                                node.setAttribute("ui.class", "lane");
                            } else if (navigableNode instanceof JunctionExitNavigableNode) {
                                node.setAttribute("ui.class", "exit");
                            } else if (navigableNode instanceof VehicleSourceNavigableNode) {
                                node.setAttribute("ui.class", "source");
                            } else if (navigableNode instanceof VehicleSinkNavigableNode) {
                                node.setAttribute("ui.class", "sink");
                            }
                        }
                        String nodeId = nodes.get(navigableNode);
                        navigableNode
                                .getNextNodes()
                                .stream()
                                .forEach(nextNavigableNode -> {
                                    if (!nodes.containsKey(nextNavigableNode)) {
                                        String nextNodeId = "node_" + nodes.size();
                                        Node nextNode = graph.addNode(nextNodeId);
                                        nodes.put(nextNavigableNode, nextNodeId);

                                        if (nextNavigableNode instanceof RoadNavigableNode) {
                                            nextNode.setAttribute("ui.class", "road");
                                        } else if (nextNavigableNode instanceof JunctionLaneNavigableNode) {
                                            nextNode.setAttribute("ui.class", "lane");
                                        } else if (nextNavigableNode instanceof JunctionExitNavigableNode) {
                                            nextNode.setAttribute("ui.class", "exit");
                                        } else if (nextNavigableNode instanceof VehicleSourceNavigableNode) {
                                            nextNode.setAttribute("ui.class", "source");
                                        } else if (nextNavigableNode instanceof VehicleSinkNavigableNode) {
                                            nextNode.setAttribute("ui.class", "sink");
                                        }
                                    }
                                    String nextNodeId = nodes.get(nextNavigableNode);
                                    graph.addEdge(nodeId + "_" + nextNodeId, nodeId, nextNodeId);
                                });
                        return navigableNode
                                .getNextNodes()
                                .stream()
                                .filter(nextNavigableNode -> !renderedNodes.contains(nextNavigableNode));
                    })
                    .collect(Collectors.toSet());
        }

        graph.display();
    }
}

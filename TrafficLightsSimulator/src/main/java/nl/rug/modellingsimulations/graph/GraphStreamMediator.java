package nl.rug.modellingsimulations.graph;

import nl.rug.modellingsimulations.model.navigablenode.*;
import nl.rug.modellingsimulations.simulation.Simulation;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.geom.Point2;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.swing.util.ImageCache;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.camera.Camera;

import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.*;
import java.util.List;
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
        updateView();
    }

    public void updateView() {
        nodes.keySet().forEach(this::updateNodeAttributes);
    }

    private void initializeGraph() {
        graph = new SingleGraph("simulation");
        graph.setAttribute(
                "ui.stylesheet",
                "node.road { " +
                        "   size: 5px;" +
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
                        "   size: 35px;" +
                        "   shadow-mode: plain; " +
                        "   shadow-color: red; " +
                        "   shadow-width: 4px; " +
                        "   shadow-offset: 0px; " +
                        "   icon-mode: at-left;" +
                        "   icon: dyn-icon;" +
                        "}" +
                        " " +
                        "node.lane { " +
                        //"   fill-color: purple; " +
                        "}" +
                        " " +
                        "node.exit { " +
                        "   size: 5px; " +
                        "}" +
                        " " +
                        "edge {" +
                        "   shadow-mode: plain; " +
                        "   shadow-width: 4px; " +
                        //"   shadow-color: orange; " +
                        "   shadow-offset: 0px; " +
                        "   arrow-shape: arrow; " +
                        "   arrow-size: 5px, 5px; " +
                        //"   shape: angle; " +
                        //"   size: 5px; " +
                        "}"
        );
        graph.setAttribute("ui.antialias");
        graph.setAttribute("ui.quality");
//        viewer = graph.display();
//        viewer.disableAutoLayout();

        viewer = new SwingViewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.disableAutoLayout();
        View view = viewer.addDefaultView(true);
        ((Component) view).addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                e.consume();
                int rotation = e.getWheelRotation();
                double factor = Math.pow(1.25, rotation);
                Camera cam = view.getCamera();
                double zoom = cam.getViewPercent() * factor;
                Point2 pxCenter  = cam.transformGuToPx(cam.getViewCenter().x, cam.getViewCenter().y, 0);
                Point3 guClicked = cam.transformPxToGu(e.getX(), e.getY());
                double newRatioPx2Gu = cam.getMetrics().ratioPx2Gu / factor;
                double x = guClicked.x + (pxCenter.x - e.getX()) / newRatioPx2Gu;
                double y = guClicked.y - (pxCenter.y - e.getY()) / newRatioPx2Gu;
                cam.setViewCenter(x, y, 0);
                cam.setViewPercent(zoom);
            }
        });
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
            nodes.put(navigableNode, node);
        }
    }

    private void updateNodeAttributes(NavigableNode navigableNode) {
        if(!this.nodes.containsKey(navigableNode))
            throw new IllegalStateException("Trying to update node's attributes that is not in graph.");

        Node node = this.nodes.get(navigableNode);

        // The color of a lane, is the color of its traffic light.
        if(navigableNode instanceof JunctionLaneNavigableNode) {
            if(((JunctionLaneNavigableNode) navigableNode).isGreenLight())
                node.setAttribute("ui.style", "fill-color: green;");
            else
                node.setAttribute("ui.style", "fill-color: red;");
        }

        // All incoming edges to this node, should have a gradient color based on the current node's load.
        int[] rgb = loadToRGB(navigableNode.getTrafficLoad());
        int color = new Color(rgb[0], rgb[1], rgb[2]).getRGB();
        String hexColor = String.format("#%06X", (0xFFFFFF & color));
        node.enteringEdges().forEach(edge -> {
                    edge.setAttribute("ui.style", "shadow-color: " + hexColor + ";");
        }
        );

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

    private int[] loadToRGB(double load) {
        double red;
        double green;
        double blue = 0;
        if(load <= 0.5) {
            // Gradient between Green -> Orange
            load *= 2; // normalize from 0 -> 1
            red = load;
            green = (1 - 0.5*load);
        } else {
            // Gradient between orange -> red
            red = 1;
            green = 0.5 - 0.5*load;
        }
        return new int[] {
                (int) (red * 255),
                (int) (green * 255),
                (int) (blue * 255)
        };
    }
}

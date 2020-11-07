package nl.rug.modellingsimulations.utilities;

import nl.rug.modellingsimulations.model.navigablenode.NavigableNode;
import nl.rug.modellingsimulations.model.navigablenode.VehicleSinkNavigableNode;

import java.util.*;
import java.util.stream.Collectors;

public class AllNodesUtility {

    private static AllNodesUtility instance = null;
    private List<NavigableNode> allNodes = null;
    private List<VehicleSinkNavigableNode> sinks = null;

    public static AllNodesUtility getInstance() {
        if(instance == null)
            instance = new AllNodesUtility();
        return instance;
    }

    public void setAllNodes(Set<NavigableNode> allNodes) {
        this.allNodes = new ArrayList<>(allNodes);
    }

    public List<NavigableNode> getAllNodes() {
        if(allNodes == null)
            throw new IllegalStateException("Trying to return all nodes of simulation, but not set.");

        return allNodes;
    }

    public List<VehicleSinkNavigableNode> getAllSinks() {
        if(sinks == null)
            sinks = this.getAllNodes().parallelStream()
                    .filter(node -> node instanceof VehicleSinkNavigableNode)
                    .map(node -> (VehicleSinkNavigableNode) node)
                    .collect(Collectors.toList());
        return sinks;
    }
}

package legacyexperiment.model.junction;

import legacyexperiment.model.road.Road;

import java.util.List;

/**
 * Interface for a Junction, where Roads enter and leave.
 */
public interface Junction {
    public void addInRoad(Road road);
    public void addOutRoad(Road road);
    public List<Road> getInRoads();
    public List<Road> getOutRoads();
}

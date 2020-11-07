package legacyexperiment.simulations;

import legacyexperiment.model.road.Road;

import java.util.List;

/**
 * First basic interface for a class that returns a world. For now it only
 * returns the Roads, later on more might be necessary (e.g. the Junctions).
 */
public interface Simulation {
    public List<Road> getRoads();
}

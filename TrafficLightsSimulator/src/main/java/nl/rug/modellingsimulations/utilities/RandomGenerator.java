package nl.rug.modellingsimulations.utilities;

import nl.rug.modellingsimulations.model.navigablenode.NavigableNode;

import java.util.List;
import java.util.Random;

/**
 * Random Number Generator
 */
public class RandomGenerator {

    private static RandomGenerator instance = null;

    private final Random random = new Random(1L);

    private RandomGenerator() {}

    public static RandomGenerator getInstance() {
        if(instance == null)
            instance = new RandomGenerator();
        return instance;
    }

    public int getIntegerBetween(int from, int to) {
        return from + random.nextInt(to - from);
    }

    public <T> T getRandomOfList(List<T> list) {
        return list.get(getIntegerBetween(0, list.size()));
    }

}

package svu.evolutionary;

import java.util.Random;

public interface SelectionMethod {
	/**
	 * Select two parents from the given population with given fitness
	 * 
	 * population.length == fitness.length
	 * 
	 * @param random Randomization
	 * @param population The population
	 * @param fitness The fitness of the population
	 * @return The indices of two parents in the population
	 */
	public int[] selectIndexes(Random random, double[][] population, double[] fitness);
}

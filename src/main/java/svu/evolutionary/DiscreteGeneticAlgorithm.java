package svu.evolutionary;

import java.util.Random;

public class DiscreteGeneticAlgorithm extends BaseGeneticAlgorithm {
	
	private double[][] values;
	
	public DiscreteGeneticAlgorithm(FitnessFunction ff, SelectionMethod sm,
			int numGenes, int numChromosomes, int elitism,
			double mutationProbability, Random random, double[][] values) {
		super(ff, sm, numGenes, numChromosomes, elitism, mutationProbability, random);
		this.values = values;
	}

	@Override
	protected double[][] initializePopulation(double[][] population) {
		for (int i = 0; i < population.length; i++)
			for (int j = 0; j < population[i].length; j++)
				population[i][j] = values[j][random.nextInt(values[j].length)];
		return population;
	}
	
	@Override
	protected double[][] mutate(int elitism, double[][] population, boolean[][] mutationIdx, Random random) {
		for (int i = elitism; i < population.length; i++) {
			for (int j = 0; j < population[i].length; j++) {
				if (mutationIdx[i][j])
					population[i][j] = values[j][random.nextInt(values[j].length)];
			}
		}
		return population;
	}
	

}

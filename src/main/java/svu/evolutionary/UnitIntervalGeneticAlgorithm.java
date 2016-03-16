package svu.evolutionary;

import java.util.Random;

public class UnitIntervalGeneticAlgorithm extends BaseGeneticAlgorithm {
	
	public UnitIntervalGeneticAlgorithm(FitnessFunction ff, SelectionMethod sm,
			int numGenes, int numChromosomes, int elitism,
			double mutationProbability, Random random) {
		super(ff, sm, numGenes, numChromosomes, elitism, mutationProbability, random);
	}

	@Override
	protected double[][] initializePopulation(double[][] population) {
		for (int i = 0; i < population.length; i++)
			for (int j = 0; j < population[i].length; j++)
				population[i][j] = random.nextDouble();
		return population;
	}
	
	@Override
	protected double[][] mutate(int elitism, double[][] population, boolean[][] mutationIdx, Random random) {
		for (int i = elitism; i < population.length; i++) {
			for (int j = 0; j < population[i].length; j++) {
				if (mutationIdx[i][j])
					population[i][j] = random.nextDouble();
			}
		}
		return population;
	}
	

}

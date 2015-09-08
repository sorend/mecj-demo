package svu.evolutionary;

import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import static svu.util.ListUtil.*;

public abstract class BaseGeneticAlgorithm {

	public static BaseGeneticAlgorithm advanceNGenerations(BaseGeneticAlgorithm ga, int generations) {
		
		Logger logger = Logger.getLogger(ga.getClass().getName());
		for (int i = 0; i < generations; i++) {
			ga.next();
			// logger.info("fitness(" + i + ") = " + prettyArray(fromIndex(ga.bestIndex(5), ga.fitness_)));
			if (logger.isLoggable(Level.FINEST)) // speed-up, no need for prettyarray
				logger.finest("fitness(" + i + ") = " + prettyArray(fromIndex(ga.bestIndex(5), ga.fitness_)));
		}
		return ga;
	}
	
	protected FitnessFunction fitnessFunction;
	protected SelectionMethod selectionMethod;
	protected Random random;
	protected int elitism;
	protected double mutationProbability;

	public double[][] population_;
	public double[] fitness_;
	
	private boolean initialized = false;

	public BaseGeneticAlgorithm(FitnessFunction ff,
			SelectionMethod sm,
			int numGenes,
			int numChromosomes,
			int elitism,
			double mutationProbability,
			Random random) {
		
		this.selectionMethod = sm;
		this.fitnessFunction = ff;
		this.random = random;
		this.elitism = elitism;
		this.mutationProbability = mutationProbability;
		this.population_ = new double[numChromosomes][numGenes];
	}
	
	protected abstract double[][] initializePopulation(double[][] population);
	
	protected abstract double[][] mutate(int elitism, double[][] population, boolean[][] mutationIdx, Random random);
	
	public void next() {
		if (!initialized) {
			population_ = initializePopulation(population_);
			initialized = true;
			fitness_ = fitnessFunction.fitness(population_);
		}
		
		double[][] newPopulation = new double[population_.length][];

		if (elitism > 0) {
			int[] sortedIdx = argsort(fitness_);
			for (int i = 0; i < sortedIdx.length; i++)
				newPopulation[i] = Arrays.copyOf(population_[sortedIdx[i]], population_[sortedIdx[i]].length);
		}
		
		for (int i = elitism; i < newPopulation.length; i++)
			newPopulation[i] = breed(population_, fitness_);

		// create index for random mutation
		boolean[][] mutationIdx = new boolean[population_.length][];
		for (int i = 0; i < population_.length; i++) {
			mutationIdx[i] = new boolean[population_[i].length];
			for (int j = 0; j < population_[i].length; j++)
				mutationIdx[i][j] = random.nextDouble() < mutationProbability;
		}
		
		newPopulation = mutate(elitism, newPopulation, mutationIdx, random);
		
		population_ = newPopulation;
		fitness_ = fitnessFunction.fitness(population_);
	}
	
	public int[] bestIndex(int n) {
		int[] sortedIdx = argsort(fitness_);
		return Arrays.copyOf(sortedIdx, n); // take n first
	}
	
	public double[] breed(double[][] population, double[] fitness) {
		// select parents
		int[] parentsIdx = selectionMethod.selectIndexes(random, population, fitness);
		double[] parent1 = population[parentsIdx[0]];
		double[] parent2 = population[parentsIdx[1]];

		// breed child (uniform crossover)
		double[] child = new double[parent1.length];
		for (int i = 0; i < child.length; i++) {
			if (random.nextBoolean())
				child[i] = parent1[i];
			else
				child[i] = parent2[i];
		}
		
		return child;
	}
}

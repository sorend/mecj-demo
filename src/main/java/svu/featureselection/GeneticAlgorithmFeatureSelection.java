package svu.featureselection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import svu.evolutionary.BaseGeneticAlgorithm;
import svu.evolutionary.DiscreteGeneticAlgorithm;
import svu.evolutionary.FitnessFunction;
import svu.evolutionary.SelectionMethod;
import svu.evolutionary.SimpleFitnessFunction;
import svu.evolutionary.SimpleFitnessWrapper;
import svu.evolutionary.TournamentSelection;
import svu.util.ListUtil;

public class GeneticAlgorithmFeatureSelection {

	private Logger logger = Logger.getLogger(GeneticAlgorithmFeatureSelection.class.getName());

	private FeaturesEvaluator evaluator;
	public Random random = new Random();
	private Map<String, Double> cache = new HashMap<String, Double>();

	public GeneticAlgorithmFeatureSelection(FeaturesEvaluator evaluator) {
		this.evaluator = evaluator;
	}
	
	public int[] selectFeatures(final double[][] trainingX, final int[] trainingY) {
		
		int m = trainingX[0].length; // number of features.

		// fitness function. Creates new MEC with specific features enabled and then check accuracy
		// on training set.
		SimpleFitnessFunction sff = new SimpleFitnessFunction() {
			@Override
			public double fitness(double[] chromosome) {
				int[] featureIdx = toFeatureIdx(chromosome); // get features represented by this chromosome
				String featureIdxKey = toCacheKey(featureIdx);
				if (!cache.containsKey(featureIdxKey)) {
					double r = evaluator.evaluate(featureIdx); // evaluate features
					cache.put(featureIdxKey, r);
				}
				return cache.get(featureIdxKey);
			}
		};

		FitnessFunction ff = new SimpleFitnessWrapper(sff);
		SelectionMethod sm = new TournamentSelection(5);
		int numGenes = m;
		int numChromosomes = 20;
		int elitism = 1;
		double mutationProbability = 0.1;
		double[][] values = new double[m][]; // initialize values
		for (int i = 0; i < m; i++)
			values[i] = new double[]{ 0.0, 1.0 };

		DiscreteGeneticAlgorithm ga = new DiscreteGeneticAlgorithm(ff, sm, numGenes, numChromosomes, elitism, mutationProbability, random, values);
		
		int generations = (int) (5 * m); // generations for each feature.
		
		BaseGeneticAlgorithm.advanceNGenerations(ga, generations);
		
		int[] idx = ga.bestIndex(1);
		double[] featuresSelected = ga.population_[idx[0]];
		int[] featureIdx = toFeatureIdx(featuresSelected);
		
		logger.info("Best fitness "+ga.fitness_[idx[0]]+" features " + ListUtil.prettyArray(featureIdx));

		return featureIdx;
	}

	//
	// Converts a chromosome into an index of selected features.
	//
	// E.g.
	//   Chromosome (1, 0, 0, 0, 1, 0, 1)
	//   FeatureIDX  0  1  2  3  4  5  6
	//
	//   Result:     0,          4,    6
	//
	private int[] toFeatureIdx(double[] x) {
		List<Integer> featureIdx = new ArrayList<Integer>();
		for (int i = 0; i < x.length; i++) // count number of features wanted.
			if (Math.abs(x[i] - 1.0) < Math.abs(x[i] - 0.0))
				featureIdx.add(i);
		return ListUtil.toArray(featureIdx);
	}
	
	private String toCacheKey(int[] featureIdx) {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < featureIdx.length; i++)
			s.append(i).append(",");
		return s.toString();
	}

}

package svu.featureselection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import svu.evolutionary.BaseGeneticAlgorithm;
import svu.evolutionary.FitnessFunction;
import svu.evolutionary.SelectionMethod;
import svu.evolutionary.SimpleFitnessFunction;
import svu.evolutionary.SimpleFitnessHelper;
import svu.evolutionary.TournamentSelection;
import svu.evolutionary.UnitIntervalGeneticAlgorithm;
import svu.util.ListUtil;

public class GeneticAlgorithmFeatureSelection {

	private Logger logger = Logger.getLogger(GeneticAlgorithmFeatureSelection.class.getName());

	private int n;
	private FeaturesEvaluator evaluator;
	public Random random = new Random();
	private Map<String, Double> cache = new HashMap<String, Double>();

	public GeneticAlgorithmFeatureSelection(int n, FeaturesEvaluator evaluator) {
		this.n = n;
		this.evaluator = evaluator;
	}
	
	public int[] selectFeatures(final double[][] trainingX, final int[] trainingY) {
		
		int m = trainingX[0].length; // number of features.

		// fitness function. Creates new MEC with specific features enabled and then check accuracy
		// on training set.
		SimpleFitnessFunction sff = new SimpleFitnessFunction() {
			@Override
			public double fitness(double[] chromosome) {
				int[] featureIdx = toFeatureIdx(GeneticAlgorithmFeatureSelection.this.n, chromosome); // get features represented by this chromosome
				String featureIdxKey = ListUtil.prettyArray(featureIdx);
				if (!cache.containsKey(featureIdxKey)) {
					double r = evaluator.evaluate(featureIdx); // evaluate features
					cache.put(featureIdxKey, r);
				}
				return cache.get(featureIdxKey);
			}
		};

		FitnessFunction ff = new SimpleFitnessHelper(sff);
		SelectionMethod sm = new TournamentSelection(5);
		int numGenes = m;
		int numChromosomes = 100;
		int elitism = 1;
		double mutationProbability = 0.25;
//		double[][] values = new double[m][]; // initialize values
//		for (int i = 0; i < m; i++)
//			values[i] = new double[]{ 0.0, 1.0 };

		UnitIntervalGeneticAlgorithm ga = new UnitIntervalGeneticAlgorithm(ff, sm, numGenes, numChromosomes, elitism, mutationProbability, random);
		
		int generations = (int) (5 * m); // generations for each feature.
		
		for (int i = 0; i < generations; i++) {
			ga.next();
			int[] best = ga.bestIndex(5);
			//System.out.println("--- Generation " + i);
			for (int j = 0; j < best.length; j++) {
				//System.out.println("fitness:" + ga.fitness_[best[j]] + " solution: " + ListUtil.prettyArray(toFeatureIdxN(n, ga.population_[best[j]])) + " c: " + ListUtil.prettyArray(ga.population_[best[j]]));
			}
		}
//		BaseGeneticAlgorithm.advanceNGenerations(ga, generations);
		
		int[] idx = ga.bestIndex(1);
		double[] featuresSelected = ga.population_[idx[0]];
		int[] featureIdx = toFeatureIdx(n, featuresSelected);
		
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
	private int[] toFeatureIdx(int dummy, double[] x) {
		List<Integer> featureIdx = new ArrayList<Integer>();
		for (int i = 0; i < x.length; i++)
			if (x[i] >= 0.5) // more than 0.5 means include this feature.
				featureIdx.add(i);
		return ListUtil.toArray(featureIdx);
	}
	
	//
	// Converts a unit interval chromosome into an index of selected features.
	//
	// E.g.
	//   n = 3
	//   Chromosome (0.5, 0.2, 0.9, 0.3, 0.1, 0.7, 0.5)
	//   FeatureIDX  0    1    2    3    4    5    6
	//   Rank        4    6    1    5    7    2    3
	//
	//   Result:     0,   1,             4   (top n=3)
	//
	private int[] toFeatureIdxN(int n, double[] x) {
		List<Integer> featureIdx = new ArrayList<Integer>();
		int[] sorted = ListUtil.argsort(x); // actually we take smallest ones
		for (int i = 0; i < n; i++) // count number of features wanted.
			featureIdx.add(sorted[i]);
		Collections.sort(featureIdx);
		return ListUtil.toArray(featureIdx);
	}

	private String toCacheKey(int[] featureIdx) {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < featureIdx.length; i++)
			s = s.append(i).append(",");
		return s.toString();
	}

}

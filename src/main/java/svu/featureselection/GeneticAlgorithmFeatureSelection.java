package svu.featureselection;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import svu.evolutionary.FitnessFunction;
import svu.evolutionary.SelectionMethod;
import svu.evolutionary.SimpleFitnessFunction;
import svu.evolutionary.SimpleFitnessHelper;
import svu.evolutionary.TournamentSelection;
import svu.evolutionary.UnitIntervalGeneticAlgorithm;
import svu.util.ListUtil;

public class GeneticAlgorithmFeatureSelection {

	private Logger logger = Logger.getLogger(GeneticAlgorithmFeatureSelection.class.getName());

	private FeaturesEvaluator evaluator;
	public Random random = new Random();
	private Map<String, Double> cache = new HashMap<String, Double>();
	public FeatureSelectionEncoding encoding = FeatureSelectionEncoding.Factory.fixed(3);

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
				int[] featureIdx = encoding.toFeaturesIdx(chromosome); // get features represented by this chromosome
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
		int[] featureIdx = encoding.toFeaturesIdx(featuresSelected);
		
		logger.info("Best fitness "+ga.fitness_[idx[0]]+" features " + ListUtil.prettyArray(featureIdx));

		return featureIdx;
	}

}

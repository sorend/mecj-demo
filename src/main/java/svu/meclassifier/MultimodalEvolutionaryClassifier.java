package svu.meclassifier;

import static svu.util.ListUtil.argsort;
import static svu.util.ListUtil.range;
import static svu.util.ListUtil.toArray;
import static svu.util.ListUtil.unique;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import svu.evolutionary.BaseGeneticAlgorithm;
import svu.evolutionary.ContinousGeneticAlgorithm;
import svu.evolutionary.SelectionMethod;
import svu.evolutionary.SimpleFitnessFunction;
import svu.evolutionary.SimpleFitnessHelper;
import svu.evolutionary.TournamentSelection;
import svu.util.ListUtil;

public class MultimodalEvolutionaryClassifier {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());

	private int numIterations;
	private DistanceFunctionFactory df;
	public int[] featureIdx = null;
	public Random random = new Random();
	
	private Map<Integer, SimpleFitnessFunction> models_;
	public int[] classes_;
	private DistanceFunction distance_;
	
	// genetic algorithm parameters
	public int numChromosomes = 100;
	public SelectionMethod sm = new TournamentSelection(5);
	public int elitism = 3;
	public double scaling = 10.0;
	public double mutationProbability = 0.1;

	public MultimodalEvolutionaryClassifier(int numIterations, DistanceFunctionFactory df) {
		this.numIterations = numIterations;
		this.df = df;
	}
	
	public MultimodalEvolutionaryClassifier featureIdx(int[] featureIdx) {
		this.featureIdx = featureIdx;
		return this;
	}
	
	private double[] decodeChromosome(double[] chromosome, int[] featureIdx, int m) {
		double[] v = new double[m];
		for (int i = 0; i < featureIdx.length; i++)
			v[featureIdx[i]] = chromosome[i];
		return v;
	}
	
	public SimpleFitnessFunction buildForClass(int clz, final double[][] data, final int[] dataIdx) {

		final int m = data[0].length;
		
		// make fitness function for the class (compare only to datapoints in the class)
		SimpleFitnessFunction sff = new SimpleFitnessFunction() {
			@Override
			public double fitness(double[] chromosome) {
				double sum = 0;
				for (int i = 0; i < dataIdx.length; i++) {
					double[] refVector = decodeChromosome(chromosome, MultimodalEvolutionaryClassifier.this.featureIdx, m);
					sum += distance_.distance(data[dataIdx[i]], refVector, MultimodalEvolutionaryClassifier.this.featureIdx);
				}
				return sum;
			}
		};

		// setup genetic algorithm parameters
		SimpleFitnessHelper ff = new SimpleFitnessHelper(sff);
		int numGenes = featureIdx.length;

		ContinousGeneticAlgorithm ga = 
				new ContinousGeneticAlgorithm(ff, sm, numGenes, numChromosomes, elitism, mutationProbability, random, scaling);

		// run genetic algorithm
		BaseGeneticAlgorithm.advanceNGenerations(ga, this.numIterations);
		
		// use best chromosome as model
		int[] idx = ga.bestIndex(1);
		final double[] model = decodeChromosome(ga.population_[idx[0]], featureIdx, m);

		logger.info("class " + clz + " model " + ListUtil.prettyArray(model) + " featureIdx " + ListUtil.prettyArray(featureIdx)); // show what we learned

		return new SimpleFitnessFunction() {
			@Override
			public double fitness(double[] data) {
				// calculate distance from model to datapoint
				return distance_.distance(model, data, MultimodalEvolutionaryClassifier.this.featureIdx); 
			}
		};
		
	}

	public MultimodalEvolutionaryClassifier fit(double[][] trainingX, int[] trainingY) {

		// initialize distance function
		distance_ = df.newFunction(trainingX, trainingY);
		// initialize models
		models_ = new HashMap<Integer, SimpleFitnessFunction>();
		// find classes
		classes_ = unique(trainingY);
		// make featureidx if not set.
		if (featureIdx == null)
			featureIdx = range(0, trainingX[0].length);
		// iterate classes
		for (int i = 0; i < classes_.length; i++) {
			int clz = classes_[i];
			List<Integer> dataIdx = new ArrayList<Integer>(); // index for examples belonging to class
			for (int k = 0; k < trainingY.length; k++)
				if (trainingY[k] == clz)
					dataIdx.add(k);

			// build and save model for class
			models_.put(clz, buildForClass(clz, trainingX, toArray(dataIdx)));
		}
		
		return this;
	}
	
	public int[] predict(double[][] testingX) {
		
		int[] result = new int[testingX.length];

		double[] R = new double[classes_.length];
		for (int i = 0; i < testingX.length; i++) {
			for (int j = 0; j < classes_.length; j++)
				R[j] = models_.get(classes_[j]).fitness(testingX[i]);
			int[] sorted = argsort(R);
			result[i] = classes_[sorted[0]];
		}
		
		return result;
	}
	
	// give confidence values for each class.
	public double[][] predictProba(double[][] testingX) {
		double[][] R = new double[testingX.length][classes_.length];
		for (int i = 0; i < testingX.length; i++) {
			for (int j = 0; j < classes_.length; j++)
				R[i][j] = models_.get(classes_[j]).fitness(testingX[i]);
		}
		return R;
	}
}

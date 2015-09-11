package svu.meclassifier;

import static svu.util.ListUtil.*;

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
import svu.evolutionary.SimpleFitnessWrapper;
import svu.evolutionary.TournamentSelection;

public class MultimodalEvolutionaryClassifier {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());

	private int numIterations;
	private DistanceFunctionFactory df;
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
	
	public SimpleFitnessFunction buildForClass(int clz, final double[][] data, final int[] dataIdx) {

		final double[][] clzData = filter(dataIdx, data);
		
		// make fitness function for the class (compare only to datapoints in the class)
		SimpleFitnessFunction sff = new SimpleFitnessFunction() {
			@Override
			public double fitness(double[] chromosome) {
				double sum = 0;
				for (int i = 0; i < clzData.length; i++)
					sum += distance_.distance(clzData[i], chromosome);
				return sum;
			}
		};

		// setup genetic algorithm parameters
		SimpleFitnessWrapper ff = new SimpleFitnessWrapper(sff);
		int numGenes = data[0].length;

		ContinousGeneticAlgorithm ga = 
				new ContinousGeneticAlgorithm(ff, sm, numGenes, numChromosomes, elitism, mutationProbability, random, scaling);

		// run genetic algorithm
		BaseGeneticAlgorithm.advanceNGenerations(ga, this.numIterations);
		
		// use best chromosome as model
		int[] idx = ga.bestIndex(1);
		final double[] model = ga.population_[idx[0]];

		logger.info("class " + clz + " model " + prettyArray(model)); // show what we learned

		return new SimpleFitnessFunction() {
			@Override
			public double fitness(double[] data) {
				return distance_.distance(model, data); // calculate distance from model to datapoint
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

package svu.proteinsequences;

import java.util.Random;

import svu.evolutionary.BaseGeneticAlgorithm;
import svu.evolutionary.DiscreteGeneticAlgorithm;
import svu.evolutionary.FitnessFunction;
import svu.evolutionary.SelectionMethod;
import svu.evolutionary.TournamentSelection;
import svu.util.ListUtil;

public class DecisionRuleClassifier {

	// GA values
	public Random random = new Random();
	public int numChromosomes = 100;
	public int elitism = 5;
	public double mutationProbability = 0.1;
	public int generations = 100;
	
	private DecisionRule[] rules;
	private double[] negativeExample;
	private int negativeDecision = 0;
	private int positiveDecision = 1;
	private int maxRules = 25;

	public DecisionRuleClassifier(double[] negativeExample) {
		this(negativeExample, null);
	}
	
	public DecisionRuleClassifier negativeDecision(int negativeDecision) {
		this.negativeDecision = negativeDecision;
		return this;
	}
	
	public DecisionRuleClassifier positiveDecision(int positiveDecision) {
		this.positiveDecision = positiveDecision;
		return this;
	}

	public DecisionRuleClassifier maxRules(int maxRules) {
		this.maxRules = maxRules;
		return this;
	}
	
	// 2, 128, 162, 130, 237, 269, 425
	public DecisionRuleClassifier(double[] negativeExample, DecisionRule[] rules) {
		this.negativeExample = negativeExample;
		this.rules = rules;
	}
	
	private DecisionRule[] decodeRules(double[] chromosome) {
		DecisionRule[] rules = new DecisionRule[maxRules];
		for (int i = 0; i < maxRules; i++) {
			rules[i] = new DecisionRule((int) chromosome[i], positiveDecision);
		}
		return rules;
	}
	
	public DecisionRuleClassifier fit(final double[][] X, final int[] Y) {
		
		if (X.length < 1)
			throw new IllegalArgumentException("len(X) < 1");

		// define which positions are available (length of chromosome)
		double[] positionValues = ListUtil.intToDoubles(ListUtil.range(0, X[0].length));

		// specify discrete values for the GA.
		double[][] values = new double[maxRules][];
		for (int i = 0; i < values.length; i++)
			values[i] = positionValues;

		// specify the fitness function for the GA
		FitnessFunction ff = new FitnessFunction(){
			@Override
			public double[] fitness(double[][] population) {
				double[] fitness = new double[population.length];
				for (int i = 0; i < population.length; i++) {

					DecisionRule[] tempRules = decodeRules(population[i]);
					
					int[] y_pred = predictInner(negativeDecision, negativeExample, tempRules, X);
					fitness[i] = 1.0 - AccuracyHelper.accuracy(Y, y_pred);
				}
				return fitness;
			}
		};
		
		SelectionMethod sm = new TournamentSelection(5);
		int numGenes = maxRules;

		// construct GA
		DiscreteGeneticAlgorithm ga = new DiscreteGeneticAlgorithm(ff, sm, numGenes, numChromosomes, elitism, mutationProbability, random, values);
		
		// run GA for some generations
		BaseGeneticAlgorithm.advanceNGenerations(ga, generations);

		// construct rules from best chromosome in current population
		this.rules = decodeRules(ga.population_[ga.bestIndex(1)[0]]);

		return this;
	}

	private static int[] predictInner(int defaultDecision, double[] normal, DecisionRule[] rules, double[][] X) {
		int[] yPredicted = new int[X.length];
		
		for (int i = 0; i < X.length; i++) {
			// System.out.println("- predicting " + i);
			boolean set = false;
			for (int j = 0; !set && j < rules.length; j++) {
				if (rules[j].isActive(normal, X[i])) {
					yPredicted[i] = rules[j].getDecision();
					set = true;
				}
			}
			if (!set)
				yPredicted[i] = defaultDecision;
		}
		
		return yPredicted;
	}
	
	public int[] predict(double[][] X) {
		if (rules == null)
			throw new IllegalArgumentException("Rules not specified. Must fit() or explicitly set rules");
		
		return predictInner(negativeDecision, negativeExample, rules, X);
	}
	
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("DecisionRuleClassifier{");
		for (int i = 0; rules != null && i < rules.length; i++)
			s.append("\n\t").append(rules[i].toString());
		
		return s.append("\n\tDEFAULT " + negativeDecision).append("\n}").toString();
	}
}

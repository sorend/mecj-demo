package svu.proteinsequences;

import svu.evolutionary.DiscreteGeneticAlgorithm;
import svu.evolutionary.FitnessFunction;
import svu.meclassifier.DistanceFunction;
import svu.meclassifier.MultimodalEvolutionaryClassifier;

public class RulesBuilderMain {
	
	public static int[] range(int a, int b) {
		int l = b - a;
		int[] vals = new int[l];
		for (int i = 0; i < l; i++)
			vals[i] = a + i;
		return vals;
	}
	
	public static void main(String[] args) throws Exception {

		int[] positionValues = range(0, 1000);
		int[] ruleCountValues = range(1, 25);
		
		int[][] values = new int[][] {
				ruleCountValues, // decide how many rules there should be
				positionValues,  // decide the position for the rule #1
				positionValues,  // decide the position for the rule #2
				positionValues,  // ...
				positionValues,
				positionValues,
				positionValues,
				positionValues,
				positionValues,
				positionValues,
				positionValues,
				positionValues,
				positionValues,
				positionValues,
				positionValues,
				positionValues,
				positionValues,
				positionValues,
				positionValues,
				positionValues,
				positionValues,
				positionValues,
				positionValues,
				positionValues,
				positionValues,
				positionValues,
		};

		FitnessFunction ff = new FitnessFunction(){
			@Override
			public double[] fitness(double[][] population) {
				// TODO Auto-generated method stub
				return null;
			}
		};
		
		// DiscreteGeneticAlgorithm ga = new DiscreteGeneticAlgorithm(ff, sm, numGenes, numChromosomes, elitism, mutationProbability, random, values);
		
		DistanceFunction df = new DistanceFunction() {
			@Override
			public double distance(double[] a, double[] b) {
				double sum = 0;
				for (int i = 0; i < a.length; i++)
					sum += Math.abs(a[i] - b[i]);
				return sum;
			}
		};
		
	}
}

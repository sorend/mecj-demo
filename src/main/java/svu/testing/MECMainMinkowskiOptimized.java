package svu.testing;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import svu.evolutionary.SimpleFitnessFunction;
import svu.heuristics.PatternSearch;
import svu.meclassifier.DistanceFunctionFactory;
import svu.meclassifier.MinkowskiDistanceFunction;
import svu.meclassifier.MultimodalEvolutionaryClassifier;
import svu.util.ListUtil;

public class MECMainMinkowskiOptimized {

	
	public static Callable<Double> buildCallable(final double[][] X, final int[] Y, final DistanceFunctionFactory df) {

		return new Callable<Double>() {
			@Override
			public Double call() throws Exception {
				// declare classifier
				MultimodalEvolutionaryClassifier mec =
						new MultimodalEvolutionaryClassifier(100, df);
				
				// make split training and testing
				int trainingSamples = (int) (0.66 * Y.length);
				int[][] trainingTestingIdx = ListUtil.shuffleSplit(ListUtil.range(Y.length), trainingSamples, new Random());
				
				double[][] X_train = ListUtil.filter(trainingTestingIdx[0], X);
				int[] Y_train = ListUtil.fromIndexInt(trainingTestingIdx[0], Y);

				double[][] X_test = ListUtil.filter(trainingTestingIdx[1], X);
				int[] Y_test = ListUtil.fromIndexInt(trainingTestingIdx[1], Y);

				// train classifier
				mec.fit(X_train, Y_train);
				
				// predict using classifier
				int[] y_pred = mec.predict(X_test);
				
				// calculate accuracy
				int correct = 0;
				for (int i = 0; i < Y_test.length; i++) {
					if (Y_test[i] == y_pred[i])
						correct++;
				}
				
				// System.out.println("correct=" + correct + " total=" + Y_test.length);
				
				return ((double)correct / Y_test.length);
			}
		};
	}
	
	public static double[] diabetesTestRunner(ExecutorService pool, int rounds, double[][] X, int[] Y, DistanceFunctionFactory df) throws Exception {

		// submit jobs
		List<Future<Double>> jobs = new ArrayList<Future<Double>>();
		for (int i = 0; i < rounds; i++)
			jobs.add(pool.submit(buildCallable(X, Y, df)));

		double res[] = new double[rounds];
		double sum = 0;
		for (int i = 0; i < rounds; i++) {
			res[i] = jobs.get(i).get();
			sum += res[i];
		}

		// calculate mean and stddev
		double mean = sum / res.length;
		double variance = 0;
		for (int i = 0; i < res.length; i++) {
			variance += (res[i] - mean) * (res[i] - mean);
		}
		variance /= (res.length - 1);
		
		double stddev = Math.sqrt(variance);
		
		return new double[]{ mean, stddev };
	}
	
	public static void diabetesTest(final ExecutorService pool) throws Exception {

		// read dataset
		Reader reader = new InputStreamReader(MECMainMinkowskiOptimized.class.getResourceAsStream("/diabetes.csv"));
		double[][] data = CSVUtil.load(reader);
		final double[][] X = CSVUtil.attributes(data);
		final int[]      Y = CSVUtil.classes(data);
		// normalize input values
		CSVUtil.normalize(X);

		// run
		final int rounds = 5;

		
		SimpleFitnessFunction ff = new SimpleFitnessFunction() {
			@Override
			public double fitness(double[] x) {
				DistanceFunctionFactory df = MinkowskiDistanceFunction.Factory(x[0]);
				try {
					double[] accuracy = diabetesTestRunner(pool, rounds, X, Y, df);
					System.out.printf("minkowski(%f) mean accuracy %.3f (+/- %.3f)\n", x[0], accuracy[0] * 100, accuracy[1] * 100);
					return 1.0 - accuracy[0];
				}
				catch (Exception e) {
					throw new RuntimeException("Failed running tests: " + e.getMessage(), e);
				}
			}
		};
		
		PatternSearch ps = new PatternSearch(ff, new double[]{ 0.0 }, new double[]{ 10.0 });
		ps.optimize(10);
		
		System.out.println("Optimized accuracy achieved: " + ((1.0 - ps.fitness_) * 100.));
		System.out.println("Optimized value for minkowski: " + ps.x_[0]);
		
	}
	
	public static void main(String[] args) throws Exception {

		// setup execution pool (use all cores of the CPU)
		int cores = Runtime.getRuntime().availableProcessors();
		ExecutorService pool = Executors.newFixedThreadPool(cores);
		
		// run the tests for diabetes
		diabetesTest(pool);
		
		pool.shutdown();
	}
}

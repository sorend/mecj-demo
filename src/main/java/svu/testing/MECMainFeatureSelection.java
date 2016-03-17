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

import svu.featureselection.FeatureSelectionHelper;
import svu.meclassifier.DistanceFunctionFactory;
import svu.meclassifier.EuclideanDistanceFunction;
import svu.meclassifier.ManhattanDistanceFunction;
import svu.meclassifier.MinkowskiDistanceFunction;
import svu.meclassifier.MultimodalEvolutionaryClassifier;
import svu.meclassifier.StoeanDistanceFunction;
import svu.proteinsequences.AccuracyHelper;
import svu.util.CSVUtil;
import svu.util.ListUtil;

public class MECMainFeatureSelection {

	public static void simpleTest() throws Exception {


		double[][] X_train = new double[][]{
				{ 1, 2, 4 },
				{ 2, 4, 8 }
		}; 
		
		int[] Y_train = new int[] {	0, 1 };

		DistanceFunctionFactory df = StoeanDistanceFunction.Factory;
		int[] featureIdx = FeatureSelectionHelper.selectWithMEC(X_train, Y_train, df, 2);

		MultimodalEvolutionaryClassifier mec = 
				new MultimodalEvolutionaryClassifier(100, df)
					.featureIdx(featureIdx)
					.fit(X_train, Y_train);
		
		System.out.println("selected Features: " + ListUtil.prettyArray(mec.featureIdx));

		double[][] X_test = new double[][]{
				{0.9, 1.7, 4.5},
				{2.1, 3.9, 7.8}
		};
		
		int[] Y_test = mec.predict(X_test);

		System.out.println("Y_actual = " + ListUtil.prettyArray(new int[]{ 0, 1 }));
		System.out.println("Y_test   = " + ListUtil.prettyArray(Y_test));
	}
	
	public static Callable<Double> buildCallable(final double[][] X, final int[] Y, final DistanceFunctionFactory df, final int[] featureIdx) {

		return new Callable<Double>() {
			@Override
			public Double call() throws Exception {
				// make split training and testing
				int trainingSamples = (int) (0.66 * Y.length);
				int[][] trainingTestingIdx = ListUtil.shuffleSplit(ListUtil.range(Y.length), trainingSamples, new Random());
				
				double[][] X_train = ListUtil.filter(trainingTestingIdx[0], X);
				int[] Y_train = ListUtil.fromIndexInt(trainingTestingIdx[0], Y);

				double[][] X_test = ListUtil.filter(trainingTestingIdx[1], X);
				int[] Y_test = ListUtil.fromIndexInt(trainingTestingIdx[1], Y);

				// train classifier
				MultimodalEvolutionaryClassifier mec = new MultimodalEvolutionaryClassifier(100, df);
				if (featureIdx != null)
					mec.featureIdx(featureIdx);

				mec.fit(X_train, Y_train);
				
				// predict using classifier
				int[] y_pred = mec.predict(X_test);

				return AccuracyHelper.accuracy(Y_test, y_pred);
			}
		};
	}
	
	public static double[] diabetesTestRunner(int rounds, double[][] X, int[] Y, DistanceFunctionFactory df, int[] featuresIdx) throws Exception {

		// setup execution pool (use all cores of the CPU)
		int cores = Runtime.getRuntime().availableProcessors();
		ExecutorService pool = Executors.newFixedThreadPool(cores);

		// submit jobs
		List<Future<Double>> jobs = new ArrayList<Future<Double>>();
		for (int i = 0; i < rounds; i++)
			jobs.add(pool.submit(buildCallable(X, Y, df, featuresIdx)));

		double res[] = new double[rounds];
		double sum = 0;
		for (int i = 0; i < rounds; i++) {
			res[i] = jobs.get(i).get();
			sum += res[i];
		}

		// shutdown pool
		pool.shutdown();

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

	public static void diabetesTest() throws Exception {

		// read dataset
		Reader reader = new InputStreamReader(MECMainFeatureSelection.class.getResourceAsStream("/diabetes.csv"));
		double[][] data = CSVUtil.load(reader);
		double[][] X = CSVUtil.attributes(data);
		int[]      Y = CSVUtil.classes(data);
		// normalize input values
		CSVUtil.normalize(X);

		// run
		int rounds = 10;
		
		// number of features: -1 = variable, >0 = how many.
		int howManyFeatures = 3;

		int[] featureIdx;

		for (int i = 0; i < rounds; i++) {
			featureIdx = FeatureSelectionHelper.selectWithMEC(X, Y, StoeanDistanceFunction.Factory, howManyFeatures);
			System.out.printf("%s selected features %s\n", "stoean", ListUtil.prettyArray(featureIdx));
		}

		featureIdx = FeatureSelectionHelper.selectWithMEC(X, Y, StoeanDistanceFunction.Factory, howManyFeatures);
		System.out.printf("%s selected features %s\n", "stoean", ListUtil.prettyArray(featureIdx));
		double[] accuracy = diabetesTestRunner(rounds, X, Y, StoeanDistanceFunction.Factory, null);
		System.out.printf("%s mean accuracy %.3f (+/- %.3f)\n", "stoean", accuracy[0] * 100, accuracy[1] * 100);
		accuracy = diabetesTestRunner(rounds, X, Y, StoeanDistanceFunction.Factory, featureIdx);
		System.out.printf("%s mean accuracy w/fs %.3f (+/- %.3f)\n", "stoean", accuracy[0] * 100, accuracy[1] * 100);

		featureIdx = FeatureSelectionHelper.selectWithMEC(X, Y, StoeanDistanceFunction.Factory, howManyFeatures);
		System.out.printf("%s selected features %s\n", "manhattan", ListUtil.prettyArray(featureIdx));
		accuracy = diabetesTestRunner(rounds, X, Y, ManhattanDistanceFunction.Factory, null);
		System.out.printf("%s mean accuracy %.3f (+/- %.3f)\n", "manhattan", accuracy[0] * 100, accuracy[1] * 100);
		accuracy = diabetesTestRunner(rounds, X, Y, ManhattanDistanceFunction.Factory, featureIdx);
		System.out.printf("%s mean accuracy w/fs %.3f (+/- %.3f)\n", "manhattan", accuracy[0] * 100, accuracy[1] * 100);

		featureIdx = FeatureSelectionHelper.selectWithMEC(X, Y, StoeanDistanceFunction.Factory, howManyFeatures);
		System.out.printf("%s selected features %s\n", "euclidean", ListUtil.prettyArray(featureIdx));
		accuracy = diabetesTestRunner(rounds, X, Y, EuclideanDistanceFunction.Factory, null);
		System.out.printf("%s mean accuracy %.3f (+/- %.3f)\n", "euclidean", accuracy[0] * 100, accuracy[1] * 100);
		accuracy = diabetesTestRunner(rounds, X, Y, EuclideanDistanceFunction.Factory, featureIdx);
		System.out.printf("%s mean accuracy w/fs %.3f (+/- %.3f)\n", "euclidean", accuracy[0] * 100, accuracy[1] * 100);

		featureIdx = FeatureSelectionHelper.selectWithMEC(X, Y, StoeanDistanceFunction.Factory, howManyFeatures);
		System.out.printf("%s selected features %s\n", "minkowski(1.5)", ListUtil.prettyArray(featureIdx));
		accuracy = diabetesTestRunner(rounds, X, Y, MinkowskiDistanceFunction.Factory(1.5), null);
		System.out.printf("%s mean accuracy %.3f (+/- %.3f)\n", "minkowski(1.5)", accuracy[0] * 100, accuracy[1] * 100);
		accuracy = diabetesTestRunner(rounds, X, Y, MinkowskiDistanceFunction.Factory(1.5), featureIdx);
		System.out.printf("%s mean accuracy w/fs %.3f (+/- %.3f)\n", "minkowski(1.5)", accuracy[0] * 100, accuracy[1] * 100);

		featureIdx = FeatureSelectionHelper.selectWithMEC(X, Y, StoeanDistanceFunction.Factory, howManyFeatures);
		System.out.printf("%s selected features %s\n", "minkowski(0.5)", ListUtil.prettyArray(featureIdx));
		accuracy = diabetesTestRunner(rounds, X, Y, MinkowskiDistanceFunction.Factory(0.5), null);
		System.out.printf("%s mean accuracy %.3f (+/- %.3f)\n", "minkowski(0.5)", accuracy[0] * 100, accuracy[1] * 100);
		accuracy = diabetesTestRunner(rounds, X, Y, MinkowskiDistanceFunction.Factory(0.5), featureIdx);
		System.out.printf("%s mean accuracy w/fs %.3f (+/- %.3f)\n", "minkowski(0.5)", accuracy[0] * 100, accuracy[1] * 100);

		featureIdx = FeatureSelectionHelper.selectWithMEC(X, Y, StoeanDistanceFunction.Factory, howManyFeatures);
		System.out.printf("%s selected features %s\n", "minkowski(3.0)", ListUtil.prettyArray(featureIdx));
		accuracy = diabetesTestRunner(rounds, X, Y, MinkowskiDistanceFunction.Factory(3.0), null);
		System.out.printf("%s mean accuracy %.3f (+/- %.3f)\n", "minkowski(3.0)", accuracy[0] * 100, accuracy[1] * 100);
		accuracy = diabetesTestRunner(rounds, X, Y, MinkowskiDistanceFunction.Factory(3.0), featureIdx);
		System.out.printf("%s mean accuracy w/fs %.3f (+/- %.3f)\n", "minkowski(3.0)", accuracy[0] * 100, accuracy[1] * 100);
	}
	
	public static void main(String[] args) throws Exception {
		
		// run the simple validation test
		simpleTest();
		
		// run the tests for diabetes
		diabetesTest();
	}
}

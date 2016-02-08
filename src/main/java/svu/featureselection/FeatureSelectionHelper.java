package svu.featureselection;

import com.sun.istack.internal.logging.Logger;

import svu.meclassifier.DistanceFunctionFactory;
import svu.meclassifier.MultimodalEvolutionaryClassifier;
import svu.proteinsequences.AccuracyHelper;
import svu.util.ListUtil;

public class FeatureSelectionHelper {

	private static Logger logger = Logger.getLogger(FeatureSelectionHelper.class);
	
	public static int[] selectWithMEC(final double[][] X, final int[] Y, final DistanceFunctionFactory dff) {

		FeaturesEvaluator evaluator = new FeaturesEvaluator() {
			@Override
			public double evaluate(int[] featureIdx) {
				// create classifier with featureIdx set.
				MultimodalEvolutionaryClassifier mec = new MultimodalEvolutionaryClassifier(5 * (featureIdx.length + 1), dff).featureIdx(featureIdx);
				// train and predict this classifier
				int[] yPredicted = mec.fit(X, Y).predict(X);
				// get accuracy of predictions
				double r = 1.0 - AccuracyHelper.accuracy(Y, yPredicted);
				// logger.info("featureIdx " + ListUtil.prettyArray(featureIdx) + " accuracy " + (1.0 - r));
				return r;
			}
		};
		
		GeneticAlgorithmFeatureSelection selection =
				new GeneticAlgorithmFeatureSelection(evaluator);
		
		return selection.selectFeatures(X, Y);
	}
}

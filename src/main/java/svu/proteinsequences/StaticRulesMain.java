package svu.proteinsequences;

import svu.util.ListUtil;


public class StaticRulesMain {
	
	public static void main(String[] args) throws Exception {

		// load data
		ProteinSequence[] data = ProteinSequence.load("/ps.data");
		// 
		double[][] X = ProteinSequence.xValues(data);
		int[] Y = ProteinSequence.yValues(data);
		
		// build static classifier
		DecisionRuleClassifier clfStatic = ProteinSequenceClassifiers.newStatic();
		
		// predict
		int[] Y_pred = clfStatic.predict(X);

		// calculate accuracy
		double accuracy = AccuracyHelper.accuracy(Y, Y_pred);

		System.out.println("Using static rules:");
		System.out.println("" + clfStatic.toString() + "\n");
		System.out.println("       Y = " + ListUtil.prettyArray(Y));
		System.out.println("  Y_pred = " + ListUtil.prettyArray(Y_pred));
		System.out.println("Accuracy = " + accuracy);
		
		DecisionRuleClassifier clfGA = ProteinSequenceClassifiers.newGenetic(data, 10);
		Y_pred = clfGA.predict(X);
		
		accuracy = AccuracyHelper.accuracy(Y, Y_pred);
		
		System.out.println("Using GA with 10 rules:");
		System.out.println("" + clfGA.toString() + "\n");
		System.out.println("       Y = " + ListUtil.prettyArray(Y));
		System.out.println("  Y_pred = " + ListUtil.prettyArray(Y_pred));
		System.out.println("Accuracy = " + accuracy);

	}
}

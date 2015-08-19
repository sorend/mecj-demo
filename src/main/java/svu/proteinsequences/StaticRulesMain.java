package svu.proteinsequences;

import svu.util.ListUtil;


public class StaticRulesMain {
	
	private static double[][] xValues(ProteinSequence[] ps) {
		double[][] X = new double[ps.length][];
		for (int i = 0; i < ps.length; i++)
			X[i] = ps[i].sequence;
		return X;
	}
	
	private static int[] yValues(ProteinSequence[] ps) {
		int[] Y = new int[ps.length];
		for (int i = 0; i < ps.length; i++)
			Y[i] = ps[i].cls;
		return Y;
	}

	public static void main(String[] args) throws Exception {

		// load data
		ProteinSequence[] data = ProteinSequence.load("/ps.data");
		
		// build static classifier
		DecisionRuleClassifier clfStatic = ProteinSequenceClassifiers.newStatic();
		
		// 
		double[][] X = xValues(data);
		int[] Y = yValues(data);

		int[] Y_pred = clfStatic.predict(X);
		
		double accuracy = AccuracyHelper.accuracy(Y, Y_pred);
		
		System.out.println("       Y = " + ListUtil.prettyArray(Y));
		System.out.println("  Y_pred = " + ListUtil.prettyArray(Y_pred));
		
		System.out.println("Accuracy = " + accuracy);
	}
}

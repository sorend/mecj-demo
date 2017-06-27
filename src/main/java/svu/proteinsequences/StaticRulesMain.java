package svu.proteinsequences;

import svu.util.AccuracyHelper;
import svu.util.ListUtil;


public class StaticRulesMain {
	
	
	public static void quickRules(ProteinSequence[] data) {

		String negative = data[0].original.replace("_", "");
		
		for (int i = 1; i < data.length; i++) {
			
			boolean found = false;

			String other = data[i].original.replace("_", "");
			for (int j = 1; !found && j < negative.length(); j++) {
				if (negative.substring(0, j).equals(other.substring(0, j)) &&
						negative.charAt(j) != other.charAt(j)) {

					found = true;
					System.out.println("Line: " + i + ", Position: " + j + ", Change: " + negative.charAt(j) + " => " + other.charAt(j));
				}
			}

			if (!found)
				System.out.println("Line: " + i + ", No Change.");
		}
		
		
	}
	
	public static void main(String[] args) throws Exception {

		// load data
		ProteinSequence[] data = ProteinSequence.load("/ps.data");
		// 
		double[][] X = ProteinSequence.xValues(data);
		int[] Y = ProteinSequence.yValues(data);
		
		quickRules(data);
		
		// build static classifier
		DecisionRuleClassifier clfStatic = ProteinSequenceClassifiers.newStatic();

		// check change positions
		for (int i = 0; i < 500; i++)
			if (clfStatic.isChangePosition(X, i))
				System.out.println("position(" + i + ") is a change position.");
		
		// predict
		int[] Y_pred = clfStatic.predict(X);

		// calculate accuracy
		double accuracy = AccuracyHelper.accuracy(Y, Y_pred);

		System.out.println("Using static rules:");
		System.out.println("" + clfStatic.toString() + "\n");
		System.out.println("       Y = " + ListUtil.prettyArray(Y));
		System.out.println("  Y_pred = " + ListUtil.prettyArray(Y_pred));
		System.out.println("Accuracy = " + accuracy);
		
		int noRules = 10;
		DecisionRuleClassifier clfGA = ProteinSequenceClassifiers.newGenetic(data, noRules);
		Y_pred = clfGA.predict(X);
		
		accuracy = AccuracyHelper.accuracy(Y, Y_pred);
		
		System.out.println("Using GA with "+noRules+" rules:");
		System.out.println("" + clfGA.toString() + "\n");
		System.out.println("       Y = " + ListUtil.prettyArray(Y));
		System.out.println("  Y_pred = " + ListUtil.prettyArray(Y_pred));
		System.out.println("Accuracy = " + accuracy);
		
		DecisionRuleClassifier clfGA2 = ProteinSequenceClassifiers.newGeneticRuleSearch(data, 25);
		Y_pred = clfGA2.predict(X);
		
		accuracy = AccuracyHelper.accuracy(Y, Y_pred);
		
		System.out.println("Using GA-rulesearch for "+noRules+" rules:");
		System.out.println("" + clfGA2.toString() + "\n");
		System.out.println("       Y = " + ListUtil.prettyArray(Y));
		System.out.println("  Y_pred = " + ListUtil.prettyArray(Y_pred));
		System.out.println("Accuracy = " + accuracy);
		
		

	}
}

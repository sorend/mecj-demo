package svu.proteinsequences;

import java.io.IOException;

public class ProteinSequenceClassifiers {

	public static DecisionRuleClassifier newStatic() throws IOException {
		
		ProteinSequence[] data = ProteinSequence.load("/ps.data");
		
		// define rules for positions: 2, 128, 162, 130, 237, 269, 425
		// we use -1 because here it's 0-indexed.
		// Søren added rules:
		//    - 457 (example 10)
		DecisionRule[] rules = new DecisionRule[]{
				new DecisionRule(2-1, 1),
				new DecisionRule(128-1, 1),
				new DecisionRule(130-1, 1),
				new DecisionRule(162-1, 1),
				new DecisionRule(237-1, 1),
				new DecisionRule(269-1, 1),
				new DecisionRule(425-1, 1),
				new DecisionRule(457-1, 1),
		};
		
		// create classifier based on rules
		//
		// data[0].sequence is the protein sequence of the first element = the normal one.
		return new DecisionRuleClassifier(data[0].sequence, rules);
		
	}
	
	public static DecisionRuleClassifier newGenetic(ProteinSequence[] data, int maxRules) throws Exception {
		
		double[][] X = ProteinSequence.xValues(data);
		int[] Y = ProteinSequence.yValues(data);

		// create classifier and "fit" rules (uses GA inside).
		return new DecisionRuleClassifier(data[0].sequence).maxRules(maxRules).fit(X, Y);
	}

	public static DecisionRuleClassifier newGeneticRuleSearch(ProteinSequence[] data, int maxRules) throws Exception {
		
		double[][] X = ProteinSequence.xValues(data);
		int[] Y = ProteinSequence.yValues(data);

		// create classifier and "fit" rules (uses GA inside).
		return new DecisionRuleClassifier(data[0].sequence).maxRules(maxRules).fit2(X, Y);
	}

}

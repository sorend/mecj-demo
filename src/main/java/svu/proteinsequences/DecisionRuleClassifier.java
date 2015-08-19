package svu.proteinsequences;

public class DecisionRuleClassifier {
	
	private DecisionRule[] rules;
	private double[] normal;
	private int defaultDecision = 0;

	public DecisionRuleClassifier(double[] normal) {
		this(normal, null);
	}
	
	
	// 2, 128, 162, 130, 237, 269, 425
	public DecisionRuleClassifier(double[] normal, DecisionRule[] rules) {
		this.normal = normal;
		this.rules = rules;
	}
	
	public DecisionRuleClassifier fit(double[][] X, int[] Y) {
		return this;
	}

	private static int[] predictInner(int defaultDecision, double[] normal, DecisionRule[] rules, double[][] X) {
		int[] yPredicted = new int[X.length];
		
		for (int i = 0; i < X.length; i++) {
			System.out.println("- predicting " + i);
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
		
		return predictInner(defaultDecision, normal, rules, X);
	}
}

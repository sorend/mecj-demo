package svu.proteinsequences;

public class DecisionRule {
	
	private static double SIGNIFICANCE = 0.00001;
	
	private int position;
	private int decision;
	
	public DecisionRule(int position, int decision) {
		this.position = position;
		this.decision = decision;
	}
	
	public boolean isActive(double[] normal, double[] current) {
		boolean val = Math.abs(normal[position] - current[position]) > SIGNIFICANCE; 
		System.out.println("comparing " + (char)((int)normal[position]) + " with " + (char)((int)current[position]) + " => " + val);
		return val;
	}
	
	public int getDecision() {
		return decision;
	}
	
	@Override
	public String toString() {
		return "IF position[" + position + "] IS NOT normal THEN " + decision;
	}
}

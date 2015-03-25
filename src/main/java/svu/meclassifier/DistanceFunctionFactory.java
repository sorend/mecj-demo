package svu.meclassifier;

public interface DistanceFunctionFactory {
	DistanceFunction newFunction(double[][] X_train, int[] Y_train);
}

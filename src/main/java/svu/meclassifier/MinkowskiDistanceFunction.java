package svu.meclassifier;

import static java.lang.Math.pow;
import static java.lang.Math.abs;

public class MinkowskiDistanceFunction implements DistanceFunction {

	public static DistanceFunctionFactory Factory(final double c) {
		return new DistanceFunctionFactory() {
			@Override
			public DistanceFunction newFunction(double[][] X_train, int[] Y_train) {
				return new MinkowskiDistanceFunction(c);
			}
		};
	}
	
	private double c;
	
	private MinkowskiDistanceFunction(double c) { this.c = c; }
	
	@Override
	public double distance(double[] a, double[] b, int[] featureIdx) {
		double sum = 0;
		for (int i = 0; i < featureIdx.length; i++) {
			sum += pow(abs(a[featureIdx[i]] - b[featureIdx[i]]), c);
		}
		return pow(sum, 1.0 / c);
	}

}

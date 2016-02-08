package svu.meclassifier;

import java.util.Arrays;

import svu.util.ListUtil;


public class StoeanDistanceFunction implements DistanceFunction {
	
	public static final DistanceFunctionFactory Factory = new DistanceFunctionFactory() {
		@Override
		public DistanceFunction newFunction(double[][] X_train, int[] Y_train) {
			double[] min = Arrays.copyOf(X_train[0], X_train[0].length);
			double[] max = Arrays.copyOf(X_train[0], X_train[0].length);
			
			for (int i = 1; i < X_train.length; i++) {
				for (int j = 0; j < min.length; j++) {
					if (X_train[i][j] < min[j]) min[j] = X_train[i][j];
					if (X_train[i][j] > max[j]) max[j] = X_train[i][j];
				}
			}
			
			double[] d = new double[min.length];
			for (int i = 0; i < d.length; i++)
				d[i] = (max[i] - min[i] == 0.0) ? 1.0 : (max[i] - min[i]);
			
			return new StoeanDistanceFunction(d);
		}
	};
	
	private double[] d;
	
	private StoeanDistanceFunction(double[] d) {
		this.d = d;
	}
	
	@Override
	public double distance(double[] a, double[] b, int[] featureIdx) {
		double sum = 0;
		for (int i = 0; i < featureIdx.length; i++) {
			sum += Math.abs(a[featureIdx[i]] - b[featureIdx[i]]) / d[featureIdx[i]];
		}
		return sum;
	}
	
}

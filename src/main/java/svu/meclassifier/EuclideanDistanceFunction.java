package svu.meclassifier;

public class EuclideanDistanceFunction implements DistanceFunction {
	
	public static DistanceFunctionFactory Factory = new DistanceFunctionFactory() {
		@Override
		public DistanceFunction newFunction(double[][] X_train, int[] Y_train) {
			return new EuclideanDistanceFunction();
		}
	};
	
	private EuclideanDistanceFunction() {}

	@Override
	public double distance(double[] a, double[] b) {
		double sum = 0;
		for (int i = 0; i < a.length; i++) {
			double v = Math.abs(a[i] - b[i]);
			sum += (v * v);
		}
		return Math.sqrt(sum);
	}

}

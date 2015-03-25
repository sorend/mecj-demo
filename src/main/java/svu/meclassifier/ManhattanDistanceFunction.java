package svu.meclassifier;

public class ManhattanDistanceFunction implements DistanceFunction {
	
	public static DistanceFunctionFactory Factory = new DistanceFunctionFactory() {
		@Override
		public DistanceFunction newFunction(double[][] X_train, int[] Y_train) {
			return new ManhattanDistanceFunction();
		}
	};
	
	private ManhattanDistanceFunction(){}

	@Override
	public double distance(double[] a, double[] b) {
		double sum = 0;
		for (int i = 0; i < a.length; i++)
			sum += Math.abs(a[i] - b[i]);
		return sum;
	}

}

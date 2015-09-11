package weka.classifiers.rules;

import svu.meclassifier.DistanceFunctionFactory;
import svu.meclassifier.EuclideanDistanceFunction;
import svu.meclassifier.ManhattanDistanceFunction;
import svu.meclassifier.MinkowskiDistanceFunction;
import svu.meclassifier.StoeanDistanceFunction;

public class DistanceFunctionHelper {

	public static DistanceFunctionFactory buildFactory(String df) {
		if (df == null)
			df = "";
		df = df.toLowerCase();
		if ("stoean".equals(df))
			return StoeanDistanceFunction.Factory;
		else if ("manhattan".equals(df))
			return ManhattanDistanceFunction.Factory;
		else if ("euclidean".equals(df))
			return EuclideanDistanceFunction.Factory;
		else if (df.startsWith("minkowski:"))
			return MinkowskiDistanceFunction.Factory(Double.parseDouble(df.substring(10)));
		else
			throw new IllegalArgumentException("Unknown distance function: " + df);
	}
	
	
}

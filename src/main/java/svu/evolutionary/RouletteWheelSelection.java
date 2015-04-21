package svu.evolutionary;

import java.util.Arrays;
import java.util.Random;

public class RouletteWheelSelection implements SelectionMethod {
	
	private int fixIndex(int idx) {
		if (idx < 0)
			return Math.abs(idx) + 1;
		else
			return idx;
	}
	
	@Override
	public int[] selectIndexes(Random random, double[][] population, double[] fitness) {
		double[] cummulative = new double[fitness.length];
		cummulative[0] = fitness[0];
		for (int i = 1; i < fitness.length; i++)
			cummulative[i] = cummulative[i - 1] + fitness[i];
		
		double max = cummulative[cummulative.length - 1];
		
		double aRand = random.nextDouble() * max,
			bRand = random.nextDouble() * max;
		
		int aIdx = fixIndex(Arrays.binarySearch(cummulative, aRand)),
			bIdx = fixIndex(Arrays.binarySearch(cummulative, bRand));

		return new int[]{ aIdx, bIdx };
	}

}

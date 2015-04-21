package svu.evolutionary;

import java.util.Random;

import svu.util.ListUtil;

public class TruncationSelection implements SelectionMethod {
	
	@Override
	public int[] selectIndexes(Random random, double[][] population, double[] fitness) {
		int[] sortedIdx = ListUtil.argsort(fitness);
		return new int[]{ sortedIdx[sortedIdx.length - 1], sortedIdx[sortedIdx.length - 2] };
	}

}

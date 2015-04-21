package svu.evolutionary;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StochasticUniversalSamplingSelection implements SelectionMethod {
	
	@Override
	public int[] selectIndexes(Random random, double[][] population, double[] fitness) {
		double sum = 0.0;
		for (int i = 0; i < fitness.length; i++)
			sum += fitness[i];

		List<Integer> selected = new ArrayList<Integer>();
		double startOffset = random.nextDouble();
		int idx = 0;
		for (int i = 0; i < fitness.length; i++) {
			double expectation = fitness[i] / (sum * 2); // 2 = select 2 parents
			while (expectation > startOffset + idx) {
				selected.add(idx);
				idx++;
			}
		}

		return new int[]{ selected.get(0), selected.get(1) };
	}

}

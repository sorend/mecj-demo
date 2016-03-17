package svu.featureselection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import svu.util.ListUtil;

public interface FeatureSelectionEncoding {

	int[] toFeaturesIdx(double[] chromosome);

	
	public static class Factory {

		public static FeatureSelectionEncoding variable() {
			return variableLengthEncoding;
		}
		
		public static FeatureSelectionEncoding fixed(int n) {
			return new FixedFeatureSelectionEncoding(n);
		}
	}
	
	public static FeatureSelectionEncoding variableLengthEncoding = new FeatureSelectionEncoding() {
		//
		// Converts a chromosome into an index of selected features.
		//
		// E.g.
		//   Chromosome (1, 0, 0, 0, 1, 0, 1)
		//   FeatureIDX  0  1  2  3  4  5  6
		//
		//   Result:     0,          4,    6
		//
		@Override
		public int[] toFeaturesIdx(double[] x) {
			List<Integer> featureIdx = new ArrayList<Integer>();
			for (int i = 0; i < x.length; i++)
				if (x[i] >= 0.5) // more than 0.5 means include this feature.
					featureIdx.add(i);
			return ListUtil.toArray(featureIdx);
		}
	};

	public static class FixedFeatureSelectionEncoding implements FeatureSelectionEncoding {
		
		private int n;
		public FixedFeatureSelectionEncoding(int n) {
			this.n = n;
		}
		
		//
		// Converts a unit interval chromosome into an index of selected features.
		//
		// E.g.
		//   n = 3
		//   Chromosome (0.5, 0.2, 0.9, 0.3, 0.1, 0.7, 0.5)
		//   FeatureIDX  0    1    2    3    4    5    6
		//   Rank        4    6    1    5    7    2    3
		//
		//   Result:     0,   1,             4   (top n=3)
		//
		@Override
		public int[] toFeaturesIdx(double[] x) {
			List<Integer> featureIdx = new ArrayList<Integer>();
			int[] sorted = ListUtil.argsort(x); // actually we take smallest ones
			for (int i = 0; i < n; i++) // count number of features wanted.
				featureIdx.add(sorted[i]);
			Collections.sort(featureIdx);
			return ListUtil.toArray(featureIdx);
		}
	}
}

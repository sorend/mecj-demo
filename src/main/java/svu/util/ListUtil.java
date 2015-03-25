package svu.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ListUtil {

	public static int[] choice(List<Integer> range, int k, Random random) {
		List<Integer> copy = new ArrayList<Integer>(range);
		Collections.shuffle(copy, random);
		int[] result = new int[k];
		for (int i = 0; i < k; i++)
			result[i] = copy.get(i);
		return result;
	}
	
	public static int[][] shuffleSplit(List<Integer> range, int k, Random random) {
		List<Integer> copy = new ArrayList<Integer>(range);
		Collections.shuffle(copy, random);
		
		int[][] result = new int[2][];
		result[0] = new int[k];
		result[1] = new int[copy.size() - k];
		
		for (int i = 0; i < k; i++)
			result[0][i] = copy.get(i);
		for (int i = k; i < copy.size(); i++)
			result[1][i - k] = copy.get(i);
		
		return result;
	}

	public static int[] fromIndexInt(int[] index, int[] values) {
		int[] result = new int[index.length];
		for (int i = 0; i < index.length; i++)
			result[i] = values[index[i]];
		return result;
	}
	
	public static boolean contains(int value, int[] a) {
		for (int i = 0; i < a.length; i++)
			if (a[i] == value)
				return true;
		return false;
	}
	public static double[] fromIndex(int[] index, double[] values) {
		double[] result = new double[index.length];
		for (int i = 0; i < index.length; i++)
			result[i] = values[index[i]];
		return result;
	}
	
	public static double[][] filter(int[] index, double[][] data) {
		double[][] result = new double[index.length][];
		for (int i = 0; i < index.length; i++)
			result[i] = data[index[i]];
		return result;
	}
	
	public static List<Double> xrange(double start, double end, double increment) {
		List<Double> list = new ArrayList<Double>();
		for (double i = start; i <= end; i += increment)
			list.add(i);
		return list;
	}
	
	public static List<Integer> range(int start, int end, int increment) {
		List<Integer> list = new ArrayList<Integer>();
		for (int i = start; i < end; i += increment) {
			list.add(i);
		}
		return list;
	}
	
	public static List<Integer> range(int end) {
		return range(0, end, 1);
	}
	
	public static int[] argsort(final double[] numbers) {
		Integer[] indexes = new Integer[numbers.length];
		for (int i = 0; i < indexes.length; i++)
			indexes[i] = i;
		Arrays.sort(indexes, new Comparator<Integer>() {
			@Override
            public int compare(final Integer i1, final Integer i2) {
                return Double.compare(numbers[i1], numbers[i2]);
            }
		});
		return asArray(indexes);
	}
	
	public static <T extends Number> int[] toArray(final List<T> a) {
		int[] b = new int[a.size()];
		ArrayList<T> arr = new ArrayList<T>(a);
		for (int i = 0; i < b.length; i++) {
			b[i] = arr.get(i).intValue();
		}
		return b;
	}
	
	public static String prettyArray(double[][] arr) {
		StringBuilder b = new StringBuilder("[");
		for (int i = 0; i < arr.length; i++)
			b.append("\t").append(prettyArray(arr[i])).append("\n");
		return b.append("]").toString();
	}
	
	public static String prettyArray(double[] arr) {
		StringBuilder b = new StringBuilder("[");
		for (int i = 0; i < arr.length; i++) {
			if (i > 0)
				b.append(", ");
			b.append(arr[i]);
		}
		return b.append("]").toString();
	}

	public static String prettyArray(int[] arr) {
		StringBuilder b = new StringBuilder("[");
		for (int i = 0; i < arr.length; i++) {
			if (i > 0)
				b.append(", ");
			b.append(arr[i]);
		}
		return b.append("]").toString();
	}

	public static <T extends Number> int[] asArray(final T... a) {
        int[] b = new int[a.length];
        for (int i = 0; i < b.length; i++) {
            b[i] = a[i].intValue();
        }
        return b;
    }
	
	public static double[][] copy(double[][] a) {
		double[][] b = new double[a.length][];
		for (int i = 0; i < a.length; i++)
			for (int j = 0; j < a[i].length; j++)
				b[i][j] = a[i][j];
		return b;
	}
	
	public static int[] unique(int[] a) {
		Map<Integer, Integer> map = new HashMap();
		
		for (int i = 0; i < a.length; i++)
			map.put(a[i], i);
		
		return toArray(new ArrayList<Integer>(map.keySet()));
	}
}

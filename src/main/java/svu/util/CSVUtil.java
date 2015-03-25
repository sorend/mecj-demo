package svu.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;

public class CSVUtil {

	public static double[][] normalize(double[][] a) {
		double[] min = Arrays.copyOf(a[0], a[0].length);
		double[] max = Arrays.copyOf(a[0], a[0].length);
		
		// find min and max
		for (int i = 1; i < a.length; i++) {
			for (int j = 0; j < min.length; j++) {
				min[j] = Math.min(min[j], a[i][j]);
				max[j] = Math.max(max[j], a[i][j]);
			}
		}

		// min-max normalize the values in a
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < min.length; j++) {
				a[i][j] = (a[i][j] - min[j]) / (max[j] - min[j]);
			}
		}
		
		return a;
	}
	
	public static double[][] load(Reader reader) throws IOException {
		
		BufferedReader br = new BufferedReader(reader);
		
		boolean first = true;
		ArrayList<double[]> list = new ArrayList<double[]>();
		
		while (true) {
			String line = br.readLine();
			if (line == null) break;
			
			String[] arr = line.split(", *");
			
			if (first) {
				first = !first;
				continue;
			}
			
			double[] vals = new double[arr.length];
			for (int i = 0; i < arr.length; i++)
				vals[i] = Double.valueOf(arr[i]).doubleValue();
			
			list.add(vals);
		}
		
		double[][] result = new double[list.size()][];
		for (int i = 0; i < result.length; i++) {
			result[i] = list.get(i);
		}

		return result;
	}
	
	public static double[][] attributes(double[][] a) {
		double[][] b = new double[a.length][];
		for (int i = 0; i < a.length; i++)
			b[i] = Arrays.copyOf(a[i], a[i].length - 1);
		return b;
	}
	
	public static int[] classes(double[][] a) {
		int[] cls = new int[a.length];
		for (int i = 0; i < a.length; i++)
			cls[i] = new Double(a[i][a[i].length - 1]).intValue();
		return cls;
	}
}

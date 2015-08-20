package svu.proteinsequences;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class ProteinSequence {
	
	public static double[][] xValues(ProteinSequence[] ps) {
		double[][] X = new double[ps.length][];
		for (int i = 0; i < ps.length; i++)
			X[i] = ps[i].sequence;
		return X;
	}
	
	public static int[] yValues(ProteinSequence[] ps) {
		int[] Y = new int[ps.length];
		for (int i = 0; i < ps.length; i++)
			Y[i] = ps[i].cls;
		return Y;
	}

	// convert string to array of doubles.
	public static double[] stringToDoubles(String sequence) {
		char[] chars = sequence.toCharArray();
		double[] vals = new double[chars.length];
		for (int i = 0; i < vals.length; i++)
			vals[i] = (int) chars[i];
		return vals;
	}

	// load a protein sequence from a classpath resource
	public static ProteinSequence[] load(String resourcePath)
			throws IOException {
		InputStream in = ProteinSequence.class
				.getResourceAsStream(resourcePath);
		return load(new InputStreamReader(in));
	}

	// load a protein sequence
	public static ProteinSequence[] load(Reader reader) throws IOException {

		List<ProteinSequence> list = new ArrayList<ProteinSequence>();
		BufferedReader br = new BufferedReader(reader);
		while (true) {
			String line = br.readLine();
			if (line == null)
				break;
			String[] a = line.split(",");
			list.add(new ProteinSequence(stringToDoubles(a[0]), Integer
					.parseInt(a[1])));
		}
		br.close();

		return list.toArray(new ProteinSequence[list.size()]);
	}

	public final double[] sequence;
	public final int cls;

	public ProteinSequence(double[] sequence, int cls) {
		this.sequence = sequence;
		this.cls = cls;
	}

}

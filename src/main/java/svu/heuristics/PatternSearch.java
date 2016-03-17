package svu.heuristics;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.Arrays;
import java.util.Random;

import svu.evolutionary.SimpleFitnessFunction;
import svu.util.ListUtil;

public class PatternSearch {
	
	public static double[] zeros(int n) {
		double[] r = new double[n];
		Arrays.fill(r, 0.0);
		return r;
	}
	
	public static double[] ones(int n) {
		double[] r = new double[n];
		Arrays.fill(r, 1.0);
		return r;
	}

	public Random rand = new Random();

	private SimpleFitnessFunction f;
	private double[] lowerBounds;
	private double[] upperBounds;

	public double[] d;
	public double[] x_;
	public double fitness_;
	
	public PatternSearch(SimpleFitnessFunction f, double[] lowerBounds, double[] upperBounds) {
		this.f = f;
		this.lowerBounds = lowerBounds;
		this.upperBounds = upperBounds;
	}

	public double[] optimize(int maxEvaluations) {
		x_ = randomSample(rand, lowerBounds, upperBounds);
		d = subtract(upperBounds, lowerBounds);
		fitness_ = f.fitness(x_); // initial fitness
		
		for (int eval = 0; eval < maxEvaluations; eval++) {
			int idx = rand.nextInt(x_.length); // select index to modify
			
			double t = x_[idx];
			x_[idx] = min(upperBounds[idx], max(lowerBounds[idx], x_[idx] + d[idx]));

			double newFitness = f.fitness(x_);
			
			if (newFitness < fitness_) { // improved
				fitness_ = newFitness;
			} 
			else {
				x_[idx] = t;
				d[idx] *= -0.5;
			}
		}
		
		return x_;
	}

	private static double[] subtract(double[] a, double[] b) {
		double[] c = new double[a.length];
		for (int i = 0; i < c.length; i++)
			c[i] = a[i] - b[i];
		return c;
	}
	
	private static double[] randomSample(Random rand, double[] lower, double[] upper) {
		double[] r = new double[lower.length];
		for (int i = 0; i < lower.length; i++) {
			r[i] = rand.nextDouble() * (upper[i] - lower[i]) + lower[i];
		}
		return r;
	}

	public static void main(String[] args) {

		// here we minimize the variance of the values
		SimpleFitnessFunction ff = new SimpleFitnessFunction() {
			@Override
			public double fitness(double[] x) {

				double s = 0.0;
				for (double i : x) s += i;
				s /= x.length;
				
				double v = 0.0;
				for (double i : x) v += (i - s) * (i - s);
				v /= x.length;

				return v;
			}
		};
		
		PatternSearch ps = new PatternSearch(ff, zeros(10), ones(10));
		ps.optimize(100);
		
		System.out.println("fitness : " + ps.fitness_);
		System.out.println("solution: " + ListUtil.prettyArray(ps.x_));
	}
	
}

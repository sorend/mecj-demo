package svu.evolutionary;

public class SimpleFitnessWrapper implements FitnessFunction {
	
	SimpleFitnessFunction sff;
	
	public SimpleFitnessWrapper(SimpleFitnessFunction sff) {
		this.sff = sff;
	}

	@Override
	public double[] fitness(double[][] population) {
		double[] result = new double[population.length];
		for (int i = 0; i < population.length; i++)
			result[i] = sff.fitness(population[i]);
		return result;
	}

}

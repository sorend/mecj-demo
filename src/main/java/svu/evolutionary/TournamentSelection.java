package svu.evolutionary;

import java.util.Random;

import static svu.util.ListUtil.*;

public class TournamentSelection implements SelectionMethod {
	
	private int tournamentSize = 5;
	
	public TournamentSelection(int tournamentSize) {
		this.tournamentSize = tournamentSize;
	}
	
	@Override
	public int[] selectIndexes(Random random, double[][] population, double[] fitness) {
		int[] tournamentMembers = choice(range(fitness.length), tournamentSize, random);
		
		double[] tournamentFitness = new double[tournamentMembers.length];
		for (int i = 0; i < tournamentMembers.length; i++)
			tournamentFitness[i] = fitness[tournamentMembers[i]];
		
		int[] tmSorted = argsort(tournamentFitness);
		return new int[]{ tmSorted[0], tmSorted[1] };
	}

}

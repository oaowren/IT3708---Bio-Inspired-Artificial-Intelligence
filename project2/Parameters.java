public class Parameters {
    
    static final int populationSize = 100;
    static final int generationSpan = 500;
    static final int eliteSize = 4;
    static final int tournamentSize = 6;
    static final int parentSelectionSize = 20;
    static final double tournamentProb = 0.9;
    static final double crossoverProbability = 0.60;
    static final double intraDepotMutationProbability = 0.20;
    static final double interDepotMutationProbability = 0.25;
    static final double interDepotMutationRate = 10;

    static final double alpha = 1000; // Discount factor for number of active vehicles
    static final double beta = 0.001; // Discount factor for total route length

}

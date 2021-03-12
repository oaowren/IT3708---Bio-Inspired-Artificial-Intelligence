public class Parameters {
    
    static final String problem = "p22";
    static final int populationSize = 40;
    static final int generationSpan = 100;
    static final int eliteSize = 5;
    static final int tournamentSize = 5;
    static final int parentSelectionSize = 15;
    static final double tournamentProb = 0.9;
    static final double crossoverProbability = 0.40;
    static final double mutationProbability = 0.30;
    static final double interDepotMutationRate = 5;
    static final double swappableCustomerDistance = 2.5;

    static final double alpha = 100; // Discount factor for number of active vehicles
    static final double beta = 0.001; // Discount factor for total route length
    static final int memoCacheMaxAge = 5;

}

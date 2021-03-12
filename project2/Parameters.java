public class Parameters {
    
    static final String problem = "p01";
    static final int populationSize = 70;
    static final int generationSpan = 1000;
    static final int eliteSize = 3;
    static final int tournamentSize = 5;
    static final int parentSelectionSize = 20;
    static final double tournamentProb = 0.9;
    static final double crossoverProbability = 0.50;
    static final double mutationProbability = 0.25;
    static final double interDepotMutationRate = 10;
    static final double swappableCustomerDistance = 2.5;

    static final double alpha = 100; // Discount factor for number of active vehicles
    static final double beta = 0.001; // Discount factor for total route length
    static final int memoCacheMaxAge = 5;

}

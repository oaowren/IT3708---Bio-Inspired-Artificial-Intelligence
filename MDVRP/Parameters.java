public class Parameters {
    
    static final String problem = "2";
    static final int populationSize = 20;
    static final int generationSpan = 10000;
    static final int eliteSize = 2;
    static final int tournamentSize = 2;
    static final int parentSelectionSize = 10;
    static final double tournamentProb = 0.9;
    static final double crossoverProbability = 0.7;
    static final double mutationProbability = 0.35;
    static final double interDepotMutationRate = 6;
    static final double swappableCustomerDistance = 0.9;

    static final double alpha = 10; // Discount factor for number of active vehicles
    static final double beta = 0.01; // Discount factor for total route length
    static final double durationPenalty = 20;
    static final double loadPenalty = 20;
    static final boolean forceMaxLoad = false;
    static final int threadPoolSize = 10;

}

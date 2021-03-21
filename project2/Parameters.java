public class Parameters {
    
    static final String problem = "p23";
    static final int populationSize = 100;
    static final int generationSpan = 5000;
    static final int eliteSize = 1;
    static final int tournamentSize = 2;
    static final int parentSelectionSize = 60;
    static final double tournamentProb = 0.8;
    static final double crossoverProbability = 0.6;
    static final double mutationProbability = 0.2;
    static final double interDepotMutationRate = 10;
    static final double swappableCustomerDistance = 2;

    static final double alpha = 10; // Discount factor for number of active vehicles
    static final double beta = 0.01; // Discount factor for total route length
    static final double durationPenalty = 10;
    static final boolean useCrowding = false;
    static final int threadPoolSize = 10;
    static final double feasibleInsertionLimit = 40;
    static final double feasibleProb = 0.8;

}

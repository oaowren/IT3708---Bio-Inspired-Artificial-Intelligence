public class Parameters {
    
    static final String problem = "p08";
    static final int populationSize = 100;
    static final int generationSpan = 5000;
    static final int eliteSize = 2;
    static final int tournamentSize = 2;
    static final int parentSelectionSize = 70;
    static final double tournamentProb = 0.8;
    static final double crossoverProbability = 0.60;
    static final double mutationProbability = 0.2;
    static final double interDepotMutationRate = 10;
    static final double swappableCustomerDistance = 0.4;

    static final double alpha = 0; // Discount factor for number of active vehicles
    static final double beta = 0.01; // Discount factor for total route length
    static final double durationPenalty = 100;
    static final int memoCacheMaxAge = 5;
    static final boolean useCrowding = true;

}

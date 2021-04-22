package Code;

public class Parameters {
    
    public static final String filename = "176035"; // Use folder name in training_images
    // Create popsize/2 initial population, is doubled on first generation
    public static final int populationSize = 20;
    public static final int generationSpan = 100;
    public static final int eliteSize = 2;
    public static final int parentSelectionSize = 15;
    public static final double tournamentProb = 0.8;
    public static final double crossoverProbability = 0.7;
    public static final double mutationProbability = 0.2;
    // Probability of merging best edge, 1-p for random edge
    public static final double mergeMutationEpsilon = 0.7;
    public static final int threadPoolSize = 10;
    // Whether or not to use NSGA or GA
    public static final boolean useGA = false;

    public static final int minimumSegmentSize = 400;
    public static final int mergeTries = 10;

    // Weighted fitness params
    public static final double edge = 0.5;
    public static final double connectivity = 0.5;
    public static final double deviation = 0.5;
}

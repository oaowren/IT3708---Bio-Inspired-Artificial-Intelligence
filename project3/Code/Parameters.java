package Code;

public class Parameters {
    
    public static final String filename = "118035"; // Use folder name in training_images
    public static final int populationSize = 6;
    public static final int generationSpan = 5;
    public static final int eliteSize = 2;
    public static final int parentSelectionSize = 4;
    public static final double tournamentProb = 0.8;
    public static final double crossoverProbability = 0.6;
    public static final double singleGeneCrossoverProb = 0.5;
    public static final double mutationProbability = 0.6;
    public static final int threadPoolSize = 10;

    public static final int minimumSegmentSize = 400;
    public static final int mergeTries = 10;

    // Debug:
    public static final boolean printEveryGeneration = true;
}

package Code;

public class Parameters {
    
    public static final String filename = "86016"; // Use folder name in training_images
    public static final int populationSize = 20;
    public static final int generationSpan = 20;
    public static final int eliteSize = 2;
    public static final int parentSelectionSize = 12;
    public static final double tournamentProb = 0.8;
    public static final double crossoverProbability = 0.7;
    public static final double singleGeneCrossoverProb = 0.5;
    public static final double mutationProbability = 0.2;
    public static final int threadPoolSize = 10;

    public static final int minimumSegmentSize = 400;
    public static final int mergeTries = 10;

    // Debug:
    public static final boolean printEveryGeneration = false;
}

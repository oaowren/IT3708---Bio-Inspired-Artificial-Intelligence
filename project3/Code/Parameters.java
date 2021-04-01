package Code;

public class Parameters {
    
    public static final String filename = "353013"; // Use folder name in training_images
    public static final String segmentColor = "g"; // Either g(green) or b(black)
    public static final int populationSize = 20;
    public static final int generationSpan = 10000;
    public static final int eliteSize = 2;
    public static final int tournamentSize = 4;
    public static final int parentSelectionSize = 10;
    public static final double tournamentProb = 0.9;
    public static final double crossoverProbability = 0.7;
    public static final double mutationProbability = 0.35;
    public static final int threadPoolSize = 10;

}

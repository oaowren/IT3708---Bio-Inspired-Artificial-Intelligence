import Code.ImageSegmentationIO;
import Code.Parameters;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import Code.GeneticAlgorithm;
import Code.Individual;


class MOEA {

    private static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Parameters.threadPoolSize);

    public static void main(String[] args) {
        ImageSegmentationIO imageIO = new ImageSegmentationIO(Parameters.filename);
        GeneticAlgorithm ga = new GeneticAlgorithm(imageIO);
        List<Individual> highestRank;
        if (Parameters.useGA){
            ga.runGA();
            List<Individual> pop = ga.getPopulation();
            pop.sort(Comparator.comparingDouble(Individual::getWeightedFitness));
            highestRank = pop.subList(0, 5);
        } else {
            ga.runNSGA();
            highestRank = ga.rankPopulation(ga.getPopulation()).get(0);
        }
        Path pathBlack = Path.of("project3/Evaluator/Student_Segmentation_Files/" + Parameters.filename + "/");
        Path pathGreen = Path.of("project3/Evaluator/Student_Segmentation_Files_Green/" + Parameters.filename + "/");

        try {
            if (!pathBlack.toFile().exists()) {
                Files.createDirectory(pathBlack);
            }
            if (!pathGreen.toFile().exists()) {
                Files.createDirectory(pathGreen);
            }
        } catch(IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        
        imageIO.deletePrevious(pathBlack);
        imageIO.deletePrevious(pathGreen); 
        for (Individual i: highestRank){
            executor.execute(()->{
                if (Parameters.mergeSmallSegments){
                    i.mergeSmallSegments(0);
                }
                imageIO.save(Parameters.filename, i, "g");
                imageIO.save(Parameters.filename, i, "b");
            });
        }
        executor.shutdown();
        while (!executor.isTerminated()){
            ;
        }
        System.out.println("FINISHED!");
        System.out.println("Number of pareto fronts: " + ga.getParetoFrontSize());
        System.exit(0);
    }
}

import Code.ImageSegmentationIO;
import Code.Parameters;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import Code.GeneticAlgorithm;
import Code.Individual;

class MOEA {

    private static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Parameters.threadPoolSize);

    public static void main(String[] args) {
        ImageSegmentationIO imageIO = new ImageSegmentationIO(Parameters.filename);
        GeneticAlgorithm ga = new GeneticAlgorithm(imageIO.getPixels());
        ga.runGA();
        List<Individual> highestRank = ga.rankPopulation(ga.getPopulation()).get(0);
        imageIO.deletePrevious("project3/Evaluator/Student_Segmentation_Files/" + Parameters.filename + "/");
        imageIO.deletePrevious("project3/Evaluator/Student_Segmentation_Files_Green/" + Parameters.filename + "/"); 
        for (Individual i: highestRank){
            executor.execute(()->{
                // i.mergeSmallSegments(0);
                imageIO.save(Parameters.filename, i, "g");
                imageIO.save(Parameters.filename, i, "b");
            });
        }
        executor.shutdown();
        while (!executor.isTerminated()){
            ;
        }
        System.out.println("FINISHED!");
        System.exit(0);
    }
}

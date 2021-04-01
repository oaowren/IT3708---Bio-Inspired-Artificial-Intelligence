import Code.ImageSegmentationIO;
import Code.Parameters;
import Code.GeneticAlgorithm;
import Code.Individual;

class MOEA {

    public static void main(String[] args) {
        ImageSegmentationIO imageIO = new ImageSegmentationIO(Parameters.filename);
        GeneticAlgorithm ga = new GeneticAlgorithm(imageIO.getPixels());
        ga.run();
        Individual i = ga.getPopulation().get(0);
        imageIO.deletePrevious("project3/Evaluator/Student_Segmentation_Files/" + Parameters.filename + "/");
        imageIO.deletePrevious("project3/Evaluator/Student_Segmentation_Files_Green/" + Parameters.filename + "/");        
        imageIO.save(Parameters.filename, i, "g");
        imageIO.save(Parameters.filename, i, "b");
    }
}

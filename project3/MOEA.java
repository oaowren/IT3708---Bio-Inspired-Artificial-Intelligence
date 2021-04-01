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
        imageIO.save(Parameters.filename, i, Parameters.segmentColor);
    }
}

package Code;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class Fitness {

    // Memoize euclidian distance between two points
    private static final HashMap<Tuple<RGB, RGB>, Double> pairMemo = new HashMap<>();

    public static double edgeValue(Segment segment) {
        int edgeValue = 0;
        for (Pixel pixel : segment.getPixels()) {
            Collection<Pixel> neighbours = pixel.getNeighbours().values();
            for (Pixel neighbour : neighbours){
                edgeValue += 
                    segment.contains(neighbour) 
                        ? 0 
                        : distance(pixel.color, neighbour.color);
            }
        }
        return -edgeValue; // This objective should be maximized. How-ever, to keep similarity with other two objectives, we convert it as subject tominimization by negating it
    }

    public static double overallEdgeValue(Individual individual){
        double edgeValue = 0;
        for (Segment s: individual.getSegments()){
            edgeValue += s.edgeValue;
        }
        return edgeValue;
    }

    public static double connectivityMeasure(Segment segment) {
        double connectivity = 0;
        for (Pixel pixel : segment.getPixels()) {
            for (Pixel neighbour : pixel.getNeighbours().values()) {
                connectivity += 
                    segment.contains(neighbour)
                        ? 0 
                        : 0.125;
            }
        }
        return connectivity;
    }

    public static double overallConnectivity(Individual individual){
        double conn = 0.0;
        for (Segment s: individual.getSegments()){
            conn += s.connectivity;
        }
        return conn;
    }

    public static double overallDeviation(Individual individual) {
        List<Segment> segments = individual.getSegments();
        double overallDeviation = 0;
        for (Segment segment : segments) {
            overallDeviation += segment.deviation;
        }
        return overallDeviation;
    }

    public static double Deviation(Segment segment){
        double overallDeviation = 0;
        RGB centroid = segment.getCentroid();
        for (Pixel pixel : segment.getPixels()) {
            overallDeviation += distance(pixel.color, centroid);
        }
        return overallDeviation;
    }

    public static double distance(RGB i, RGB j) {
        double result = Math.sqrt(Math.pow(Math.abs(j.r-i.r), 2) 
                                + Math.pow(Math.abs(j.g-i.g), 2) 
                                + Math.pow(Math.abs(j.b-i.b), 2));
        return result;
    }
}

package Code;

import java.util.Collection;
import java.util.HashMap;

public class Fitness {

    // Memoize euclidian distance between two points
    private static final HashMap<Tuple<RGB, RGB>, Double> pairMemo = new HashMap<>();

    public static double edgeValue(Segment segment) {
        int edgeValue = 0;
        for (Pixel pixel : segment.getPixels()) {
            for (Pixel neighbour : pixel.getNeighbours().values()) {
                edgeValue += 
                    segment.getPixels().contains(neighbour) 
                        ? 0 
                        : distance(pixel.color, neighbour.color);
            }
        }
        return -edgeValue; // This objective should be maximized. How-ever, to keep similarity with other two objectives, we convert it as subject tominimization by negating it
    }

    public static double connectivityMeasure(Segment segment) {
        int connectivity = 0;
        for (Pixel pixel : segment.getPixels()) {
            for (Integer neighbourKey : pixel.getNeighbours().keySet()) {
                connectivity += 
                    segment.getPixels().contains(pixel.getNeighbours().get(neighbourKey)) 
                        ? 0 
                        : 1 / neighbourKey; // TODO: Skal det være L her? Se forskjell på de to PDF-ene
            }
        }
        return connectivity;
    }

    public static double overallDeviation(Collection<Segment> segments) {
        int overallDeviation = 0;
        for (Segment segment : segments) {
            for (Pixel pixel : segment.getPixels()) {
                overallDeviation += distance(pixel.color, segment.getCentroid());
            }
        }
        return overallDeviation;
    }

    public static double distance(RGB i, RGB j) {
        Tuple<RGB, RGB> pair = new Tuple<>(i, j);
        if (pairMemo.containsKey(pair)) {
            return pairMemo.get(pair);
        }
        double result = Math.sqrt(Math.pow(Math.abs(j.r-i.r), 2) + Math.pow(Math.abs(j.g-i.g), 2) + Math.pow(Math.abs(j.b-i.b), 2));
        pairMemo.put(pair, result);
        return result;
    }
}

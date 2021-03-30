package Code;

import java.util.HashMap;

public class Fitness {

    // Memoize euclidian distance between two points
    private static final HashMap<Tuple<RGB, RGB>, Double> pairMemo = new HashMap<>();

    public double edgeValue(Segment segment) {
        int edgeValue = 0;
        for (Pixel pixel : segment.getPixels()) {
            for (Pixel neighbour : pixel.getNeighbours()) {
                edgeValue += 
                    segment.getPixels().contains(neighbour) 
                        ? 0 
                        : distance(pixel.color, neighbour.color);
            }
        }
        return edgeValue(segment);
    }

    public double connectivityMeasure() {
        return 0.0;
    }

    public double overallDeviation() {
        return 0.0;
    }

    public double distance(RGB i, RGB j) {
        Tuple<RGB, RGB> pair = new Tuple<>(i, j);
        if (pairMemo.containsKey(pair)) {
            return pairMemo.get(pair);
        }
        double result = Math.sqrt(Math.pow(Math.abs(j.r-i.r), 2) + Math.pow(Math.abs(j.g-i.g), 2) + Math.pow(Math.abs(j.b-i.b), 2));
        pairMemo.put(pair, result);
        return result;
    }
}

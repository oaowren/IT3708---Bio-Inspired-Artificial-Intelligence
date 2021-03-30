package Code;

import java.util.HashMap;

public class Fitness {

    // Memoize euclidian distance between two points
    private static final HashMap<Tuple<RGB, RGB>, Double> pairMemo = new HashMap<>();

    public double edgeValue() {
        return 0.0;
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

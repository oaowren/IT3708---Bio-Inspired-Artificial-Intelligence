import java.util.Collection;

public class Fitness {

    public static double edgeValue(Segment segment) {
        int edgeValue = 0;
        for (Pixel pixel : segment.getPixels()) {
            Collection<Pixel> neighbours = pixel.getNeighbours().values();
            for (Pixel neighbour : neighbours){
                edgeValue += 
                    segment.contains(neighbour) ? 0 : distance(pixel.color, neighbour.color);
            }
        }
        return -edgeValue; 
        /* This objective should be maximized. However, to keep similarity with other two objectives,
           we convert it as subject tominimization by negating it */
    }

    public static double overallEdgeValue(Individual individual) {
        return individual.getSegments()
                         .stream()
                         .map(segment -> segment.edgeValue)
                         .reduce(0.0, (total, element) -> total + element);
    }

    public static double connectivity(Segment segment) {
        double connectivity = 0;
        for (Pixel pixel : segment.getPixels()) {
            for (Pixel neighbour : pixel.getNeighbours().values()) {
                connectivity += 
                    segment.contains(neighbour) ? 0 : 0.125;
            }
        }
        return connectivity;
    }

    public static double overallConnectivity(Individual individual) {
        return individual.getSegments()
                         .stream()
                         .map(segment -> segment.connectivity)
                         .reduce(0.0, (total, element) -> total + element);
    }

    public static double overallDeviation(Individual individual) {
        return individual.getSegments()
                         .stream()
                         .map(segment -> segment.deviation)
                         .reduce(0.0, (total, element) -> total + element);
    }

    public static double deviation(Segment segment) {
        return segment.getPixels()
                      .stream()
                      .map(pixel -> distance(pixel.color, segment.getCentroid()))
                      .reduce(0.0, (total, element) -> total + element);
    }

    public static double distance(RGB i, RGB j) {
        return Math.sqrt(
            Math.pow(Math.abs(j.r-i.r), 2) 
          + Math.pow(Math.abs(j.g-i.g), 2) 
          + Math.pow(Math.abs(j.b-i.b), 2)
        );
    }
}

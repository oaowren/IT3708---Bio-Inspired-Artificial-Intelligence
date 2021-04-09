package Code;

public class Edge implements Comparable<Edge>{
    public Pixel from, to;
    public double distance;

    public Edge(Pixel from, Pixel to){
        this.from = from;
        this.to = to;
        this.distance = Fitness.distance(from.color, to.color);
    }

    @Override
    public int compareTo(Edge e) {
        // Sorts by worse first
        return Double.compare(e.distance, this.distance);
    }

}

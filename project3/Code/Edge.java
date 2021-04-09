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
        if (this.distance > e.distance) return 1;
        if (e.distance > this.distance) return -1;
        return 0;
        // return this.distance - e.distance;
        // // Sorts by worse first
        // return Double.compare(e.distance, this.distance);
    }

}

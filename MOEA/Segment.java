import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Segment {

    private RGB centroid; // μk
    private Set<Pixel> pixels;
    public final double connectivity, edgeValue, deviation;

    public Segment(Set<Pixel> pixels) {
        this.pixels = pixels;
        setCentroid(findCentroid());
        this.connectivity = Fitness.connectivity(this);
        this.edgeValue = Fitness.edgeValue(this);
        this.deviation = Fitness.deviation(this);
    }

    public Segment(){
        this.pixels = new HashSet<>();
        setCentroid(findCentroid());
        this.connectivity = Fitness.connectivity(this);
        this.edgeValue = Fitness.edgeValue(this);
        this.deviation = Fitness.deviation(this);
    }

    public boolean contains(Pixel pixel) {
        return pixel != null
            ? pixels.contains(pixel)
            : false;
    }

    public RGB getCentroid() {
        return this.centroid;
    }

    public void setCentroid(RGB centroid) {
        this.centroid = centroid;
    }

    public List<Pixel> getPixels() {
        return new ArrayList<>(this.pixels);
    }

    public void setPixels(Set<Pixel> pixels) {
        this.pixels = pixels;
    }

    public void addPixels(Set<Pixel> pixels){
        this.pixels.addAll(pixels);
    }

    public RGB findCentroid(){
        int r = 0;
        int g = 0;
        int b = 0;
        int segmentSize = this.pixels.size();
        for (Pixel pixel: this.pixels) {
            r += pixel.color.r;
            g += pixel.color.g;
            b += pixel.color.b;
        }
        RGB centroid = new RGB(r/segmentSize, g/segmentSize, b/segmentSize);
        return centroid;
    }

}

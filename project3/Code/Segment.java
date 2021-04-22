package Code;

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
        this.connectivity = Fitness.connectivityMeasure(this);
        this.edgeValue = Fitness.edgeValue(this);
        this.deviation = Fitness.deviation(this);
    }

    public Segment(){
        this.pixels = new HashSet<>();
        setCentroid(findCentroid());
        this.connectivity = Fitness.connectivityMeasure(this);
        this.edgeValue = Fitness.edgeValue(this);
        this.deviation = Fitness.deviation(this);
    }

    public boolean contains(Pixel pixel){
        if (pixel == null){
            return false;
        }
        return this.pixels.contains(pixel);
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
        for (Pixel p: this.pixels) {
            r += p.color.r;
            g += p.color.g;
            b += p.color.b;
        }
        RGB centroid = new RGB(r/segmentSize, g/segmentSize, b/segmentSize);
        return centroid;
    }

}

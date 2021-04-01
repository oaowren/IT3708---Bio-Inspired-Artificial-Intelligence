package Code;

import java.util.ArrayList;
import java.util.List;

public class Segment {

    private RGB centroid; // μk
    private List<Pixel> pixels;

    public Segment(List<Pixel> pixels){
        this.pixels = pixels;
    }

    public Segment(){
        this.pixels = new ArrayList<>();
    }

    public boolean contains(Pixel pixel){
        return this.pixels.contains(pixel);
    }

    public RGB getCentroid() {
        return this.centroid;
    }
    public Pixel getPixel(int i) {
        return this.pixels.get(i);
    }

    public void setCentroid(RGB centroid) {
        this.centroid = centroid;
    }

    public List<Pixel> getPixels() {
        return new ArrayList<>(this.pixels);
    }

    public void setPixels(List<Pixel> pixels) {
        this.pixels = pixels;
    }

    public void addPixels(List<Pixel> pixels){
        this.pixels.addAll(pixels);
    }

}

package Code;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Pixel {
    
    public final RGB color;
    public final int x;
    public final int y;
    private final Set<Pixel> neighbours = new HashSet<>(); // F

    public Pixel(RGB color, int x, int y) {
        this.color = color;
        this.x = x;
        this.y = y;
    }

    public Set<Pixel> getNeighbours() {
        return new HashSet<>(neighbours);
    }

    public void addNeighbour(Pixel neighbour) {
        this.neighbours.add(neighbour);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Pixel)) {
            return false;
        }
        Pixel pixel = (Pixel) o;
        return Objects.equals(color, pixel.color) && x == pixel.x && y == pixel.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, x, y);
    }

}

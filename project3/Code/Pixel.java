package Code;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Pixel {
    
    public final RGB color;
    public final int x;
    public final int y;
    private final Map<Integer, Pixel> neighbours = new HashMap<>(); // F

    public Pixel(RGB color, int x, int y) {
        this.color = color;
        this.x = x;
        this.y = y;
    }

    public Map<Integer, Pixel> getNeighbours() {
        return new HashMap<>(neighbours);
    }

    public void addNeighbour(int key, Pixel neighbour) {
        this.neighbours.put(key, neighbour);
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

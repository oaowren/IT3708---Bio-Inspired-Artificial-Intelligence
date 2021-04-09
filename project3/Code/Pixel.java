package Code;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Pixel {
    
    public final RGB color;
    public final int x;
    public final int y;
    private Map<Integer, Pixel> neighbours = new HashMap<>(); // F

    public Pixel(RGB color, int x, int y) {
        this.color = color;
        this.x = x; // Position on width
        this.y = y; // Position on height
    }

    public Map<Integer, Pixel> getNeighbours() {
        return new HashMap<>(neighbours);
    }

    public void setNeighbours(Map<Integer, Pixel> neighbours) {
        this.neighbours = neighbours;
    }

    public Map<Integer, Pixel> getCardinalNeighbours(){
        Map<Integer, Pixel> temp = new HashMap<>();
        for (int i=1; i<5; i++){
            if (this.neighbours.get(i) != null){
                temp.put(i, this.neighbours.get(i));
            }
        }
        return temp;
    }

    public Pixel getCardinalNeighbour(Gene gene) {
        return switch(gene) {
            case RIGHT -> neighbours.get(1);
            case LEFT  -> neighbours.get(2);
            case UP    -> neighbours.get(3);
            case DOWN  -> neighbours.get(4);
            case NONE  -> this;
        };
    }

    public Pixel getCardinalNeighbour(int direction){
        return direction == 0 ? this : neighbours.get(direction);
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

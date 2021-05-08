import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.stream.Collectors;

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

    public List<Gene> getValidGenes(){
        return getCardinalNeighbours().entrySet()
                                      .stream()
                                      .filter(entry -> entry.getValue() != null)
                                      .map(Entry::getKey)
                                      .map(Gene::fromNumber)
                                      .collect(Collectors.toList());
    }

    public Map<Integer, Pixel> getCardinalNeighbours() {
        return neighbours.entrySet()
                         .stream()
                         .filter(entry -> entry.getKey() < 5 && entry.getValue() != null)
                         .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
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

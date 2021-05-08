import java.util.Arrays;
import java.util.List;

public enum Gene {
    RIGHT,
    LEFT,
    UP,
    DOWN,
    NONE;

    public static Gene fromNumber(int number) {
        return switch(number) {
            case  1 -> RIGHT;
            case  2 -> LEFT;
            case  3 -> UP;
            case  4 -> DOWN;
            case  0 -> NONE;
            default -> throw new IllegalArgumentException("Unexpected gene value: " + number);
        };
    }

    public static Gene fromUnitVector(int x, int y) {
        return switch(x) {
            case  1 -> RIGHT;
            case -1 -> LEFT;
            case  0 -> switch(y) {
                case  1 -> DOWN;
                case -1 -> UP;
                case  0 -> NONE;
                default -> throw new IllegalArgumentException("Not a unit vector: " + x + " " + y);
            };
            default -> throw new IllegalArgumentException("Not a unit vector: " + x + " " + y);
        };
    }

    public static Gene random() {
        return values()[Utils.randomInt(values().length)];
    }

    public static List<Gene> cardinalDirections() {
        return Arrays.asList(new Gene[] { RIGHT, LEFT, UP, DOWN });
    }
}

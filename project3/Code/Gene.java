package Code;

import java.util.Arrays;
import java.util.List;

public enum Gene {
    RIGHT,
    LEFT,
    UP,
    DOWN,
    NONE;

    public static Gene getGene(int number) {
        return switch(number) {
            case 1 -> RIGHT;
            case 2 -> LEFT;
            case 3 -> UP;
            case 4 -> DOWN;
            case 0 -> NONE;
            default -> throw new IllegalArgumentException("Unexpected value: " + number);
        };
    }

    public static Gene getRandomGene() {
        return values()[Utils.randomInt(values().length)];
    }

    public static List<Gene> cardinalDirections() {
        return Arrays.asList(new Gene[] {RIGHT, LEFT, UP, DOWN});
    }
}

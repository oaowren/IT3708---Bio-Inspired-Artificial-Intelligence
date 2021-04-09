package Code;

public enum Gene {
    RIGHT,
    LEFT,
    UP,
    DOWN,
    NONE;
    
    public static Gene getRandomGene() {
        return values()[Utils.randomInt(values().length)];
    }
}

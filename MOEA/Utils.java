import java.util.Map;
import java.util.Random;

public class Utils {

    private static Random random = new Random();
    
    public static int randomInt(int lower, int upper){
        return lower + randomInt(upper - lower);
    }

    public static int randomInt(int upper){
        return random.nextInt(upper);
    }

    public static double randomDouble(){
        return random.nextDouble();
    }

    public static <T> T pickRandom(T item1, T item2) {
        return randomDouble() < 0.5 ? item1 : item2;
    }

    public static int pixelToGenotypeIndex(int x, int y, int rowLength){
        return rowLength * y + x;
    }

    public static Tuple<Integer, Integer> genotypeIndexToPixelCoordinates(int i, int rowLength){
        int x = i % rowLength;
        int y = Math.floorDiv(i, rowLength);
        return new Tuple<>(x, y);
    }
}

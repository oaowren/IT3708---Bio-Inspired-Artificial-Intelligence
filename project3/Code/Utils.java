package Code;

import java.util.Map;
import java.util.Random;

public class Utils {

    private static Random random = new Random();

    public static Map<Gene, Tuple<Integer, Integer>> mapGeneToNeighbour = Map.of(
        Gene.RIGHT, new Tuple<>(1, 0),
        Gene.LEFT, new Tuple<>(-1, 0),
        Gene.UP, new Tuple<>(0, -1),
        Gene.DOWN, new Tuple<>(0, 1),
        Gene.NONE, new Tuple<>(0,0)
    );
    public static Map<Tuple<Integer, Integer>, Gene> mapNeighbourToGene = Map.of(
        new Tuple<>(1, 0), Gene.RIGHT,
        new Tuple<>(-1, 0), Gene.LEFT, 
        new Tuple<>(0, -1), Gene.UP, 
        new Tuple<>(0, 1), Gene.DOWN,
        new Tuple<>(0,0), Gene.NONE
    );

    public static int randomInt(int lower, int upper){
        return lower + randomInt(upper - lower);
    }

    public static int randomInt(int upper){
        return random.nextInt(upper);
    }

    public static double randomDouble(){
        return random.nextDouble();
    }
}

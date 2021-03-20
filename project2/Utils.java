import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import DataClasses.Customer;
import DataClasses.Tuple;

public class Utils {

    private static final Random rand = new Random();

    // Pick a random item from a list that satisfies a condition
    public static <T> T randomPick(List<T> list, Predicate<T> predicate) {
        List<T> listCopy = new ArrayList<>(list);
        Collections.shuffle(listCopy);
        int index = 0;
        T pick = listCopy.get(index);
        while (!predicate.test(pick)) {
            if (index >= listCopy.size()) {
                return null;
            }
            pick = listCopy.get(index);
            index++;
        }
        return pick;
    }

    public static double randomDouble(){
        return rand.nextDouble();
    }

    public static int randomInt(int bound){
        return rand.nextInt(bound);
    }

    public static Tuple<Integer, Integer> randomCutpoints(int bound){
        int cutPoint1 = Utils.randomInt(bound);
        int cutPoint2 = Utils.randomInt(bound); 
        while (cutPoint1 == cutPoint2) {
            cutPoint2 = Utils.randomInt(bound);
        }
        return new Tuple<>(cutPoint1, cutPoint2);
    }

    public static List<Customer> allSwappableCustomers(Depot depot) {
        List<Customer> copy = new ArrayList<>(depot.getSwappableCustomers());
        copy.retainAll(depot.getAllCustomersInVehicles());
        return copy;
    }
}

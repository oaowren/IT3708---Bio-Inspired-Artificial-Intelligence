import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import DataClasses.Customer;

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

    public static List<Customer> allSwappableCustomers(Depot depot) {
        List<Customer> allCustomersInDepot = depot.getAllCustomersInVehicles();
        return depot.getSwappableCustomers().stream()
                                            .filter(c -> allCustomersInDepot.contains(c))
                                            .collect(Collectors.toList());
    }

    public static Individual select(List<Individual> arr, int index, boolean route){
        if (arr.size()==0){
            return null;
        }
        List<Individual> left = new ArrayList<>();
        List<Individual> right = new ArrayList<>();
        Individual pivot = arr.get(0);
        for (int i=1; i<arr.size();i++){
            Individual val = arr.get(i);
            double comp;
            if (!route){
                comp = pivot.getFitness() - val.getFitness();
            }else {
                comp = Fitness.getIndividualRouteFitness(pivot) - Fitness.getIndividualFitness(val);
            }
            if (comp > 0){
                left.add(val);
            } else {
                right.add(val);
            }
        }
        if (left.size() == index){
            return pivot;
        } 
        if (left.size() > index){
            return select(left, index, route);
        }
        return select(right, index - (left.size()+1), route);
    }
}

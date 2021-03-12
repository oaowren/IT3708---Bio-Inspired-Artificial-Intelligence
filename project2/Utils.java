import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Utils {

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

    public static List<Customer> allSwappableCustomers(Depot depot) {
        List<Customer> allCustomersInDepot1 = depot.getAllCustomersInVehicles();
        return depot.getSwappableCustomers().stream()
                                            .filter(c -> allCustomersInDepot1.contains(c))
                                            .collect(Collectors.toList());
    }
}

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

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
}

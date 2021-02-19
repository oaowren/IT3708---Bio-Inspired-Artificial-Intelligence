import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {

    public static List<List<Integer>> readDataFile(String filename) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(Utils.class.getClass().getResource(filename).toURI()));
            return lines
                    .stream().map(line -> Arrays.asList(line.split("\s+")).stream()
                            .map(string -> Integer.parseInt(string)).collect(Collectors.toList()))
                    .collect(Collectors.toList());
        } catch (IOException | URISyntaxException error) {
            System.out.println(error.toString());
            return null;
        }
    }
    


    public static void processDataSet(List<List<Integer>> dataSet) {
        int maxNumOfVehicles = dataSet.get(0).get(0);
        int totalNumOfCustomers = dataSet.get(0).get(1);
        int numberOfDepots = dataSet.get(0).get(2);

        List<Depot> depots = new ArrayList<>();
        for (int i = 1; i <= numberOfDepots; i++) {
            int endIndex = dataSet.size() - 1 - numberOfDepots + i;
            int maxRouteDuration = dataSet.get(i).get(0);
            int maxVehicleLoad = dataSet.get(i).get(1);
            depots.add(new Depot(i, maxNumOfVehicles, maxRouteDuration, maxVehicleLoad, dataSet.get(endIndex).get(1), dataSet.get(endIndex).get(2)));
        }


    }
}


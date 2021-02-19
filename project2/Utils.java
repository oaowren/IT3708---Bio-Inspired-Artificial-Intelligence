import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {

    public static List<List<String>> readDataFile(String filename) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(Utils.class.getClass().getResource(filename).toURI()));
            return lines.stream().map(line -> Arrays.asList(line.split("\s+"))).collect(Collectors.toList());
        } catch (IOException | URISyntaxException error) {
            System.out.println(error.toString());
            return null;
        }
    }

    public static void processDataSet(List<List<String>> dataSet) {
        int maxNumOfVehicles = Integer.parseInt(dataSet.get(0).get(0));
        int totalNumOfCustomers = Integer.parseInt(dataSet.get(0).get(1));
        int numberOfDepots = Integer.parseInt(dataSet.get(0).get(2));

        for (int i = 1; i <= numberOfDepots; i++) {
            
        }
    }
}


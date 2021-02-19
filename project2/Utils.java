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
        int numOfCustomers = dataSet.get(0).get(1);
        int numberOfDepots = dataSet.get(0).get(2);
        int depotIndexStartEnd = dataSet.size() - 1 - numberOfDepots;

        List<Depot> depots = new ArrayList<>();
        for (int i = 1; i <= numberOfDepots; i++) {
            int endIndex = depotIndexStartEnd + i;
            int maxRouteDuration = dataSet.get(i).get(0);
            int maxVehicleLoad = dataSet.get(i).get(1);
            depots.add(new Depot(i, maxNumOfVehicles, maxRouteDuration, maxVehicleLoad, dataSet.get(endIndex).get(1), dataSet.get(endIndex).get(2)));
        }

        List<Customer> customers = new ArrayList<>();
        for (List<Integer> customerRow : dataSet.subList(numberOfDepots+1, depotIndexStartEnd)) {
            customers.add(new Customer(customerRow.get(0), customerRow.get(1), customerRow.get(2), customerRow.get(3), customerRow.get(4)));
        }
    }

    public static void writeResults(List<Depot> depots, String filename){
        try{
            for (Depot d:depots){
                for (Vehicle v:d.getAllVehicles()){
                    Files.writeString(Paths.get(filename), v.getCustomerSequence()+"\n");
                }
            }
        } catch (IOException error) {
            System.out.println(error.toString());
        }
    }
}


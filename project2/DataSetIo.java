import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DataSetIo {
    
    List<Depot> depots;
    List<Customer> customers;

    public static List<List<Integer>> readDataFile(String filename) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filename));
            return lines.stream()
                    .map(line -> 
                        Arrays.asList(line.trim().split("\s+"))
                              .stream()
                              .map(string -> Integer.parseInt(string))
                              .collect(Collectors.toList())
                    )
                    .collect(Collectors.toList());
        } catch (IOException error) {
            System.out.println(error.toString());
            return null;
        }
    }

    public static void writeResults(List<Depot> depots, String filename){
        List<String> lines = depots.stream()
                                    .flatMap(d -> d.getVehicleRoutes().stream())
                                    .collect(Collectors.toList());
        try{
            Files.write(Paths.get(filename), lines);
        } catch (IOException error) {
            System.out.println(error.toString());
        }
    }
    
    public void processDataSet(List<List<Integer>> dataSet) {
        int maxNumOfVehicles = dataSet.get(0).get(0);
        int numOfCustomers = dataSet.get(0).get(1);
        int numberOfDepots = dataSet.get(0).get(2);
        int depotIndexStartEnd = dataSet.size() - numberOfDepots;

        depots = new ArrayList<>();
        for (int i = 1; i <= numberOfDepots; i++) {
            int endIndex = depotIndexStartEnd + i - 1;
            int maxRouteDuration = dataSet.get(i).get(0);
            int maxVehicleLoad = dataSet.get(i).get(1);
            depots.add(new Depot(i, maxNumOfVehicles, maxRouteDuration, maxVehicleLoad, dataSet.get(endIndex).get(1),
                    dataSet.get(endIndex).get(2)));
        }

        customers = new ArrayList<>();
        for (List<Integer> customerRow : dataSet.subList(numberOfDepots + 1, depotIndexStartEnd)) {
            customers.add(new Customer(customerRow.get(0), customerRow.get(1), customerRow.get(2), customerRow.get(3),
                    customerRow.get(4)));
        }
    }

    public static void main(String[] args) {
        DataSetIo dataSet = new DataSetIo();
        dataSet.processDataSet(DataSetIo.readDataFile("project2/Data Files/p01"));
        System.out.println(dataSet.depots);
        System.out.println(dataSet.customers);
    }
}


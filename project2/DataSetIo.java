import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import DataClasses.Customer;

public class DataSetIo {
    
    private List<List<Integer>> dataSet;
    private int maxNumOfVehicles;
    private int numOfCustomers;
    private int numberOfDepots;
    private int depotIndexStartEnd;

    public void readDataFile(String filename) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filename));
            this.dataSet = lines.stream()
                    .map(line -> 
                        Arrays.asList(line.trim().split("\s+"))
                              .stream()
                              .map(string -> Integer.parseInt(string))
                              .collect(Collectors.toList())
                    )
                    .collect(Collectors.toList());
            this.maxNumOfVehicles = dataSet.get(0).get(0);
            this.numOfCustomers = dataSet.get(0).get(1);
            this.numberOfDepots = dataSet.get(0).get(2);
            this.depotIndexStartEnd = dataSet.size() - numberOfDepots;
        } catch (IOException error) {
            System.out.println(error.toString());
        }
    }

    public static void writeResults(Individual individual, String filename) {
        List<Depot> depots = individual.getDepots();

        List<String> lines = new ArrayList<>();
        lines.add(Double.toString(individual.getFitness()));
        lines.addAll(depots.stream().flatMap(d -> d.getVehicleRoutes().stream()).collect(Collectors.toList()));
        try {
            Files.write(Paths.get(filename), lines);
        } catch (IOException error) {
            System.out.println(error.toString());
        }
    }
    
    public List<Depot> getDepots() {
        List<Depot> depots = new ArrayList<>();
        for (int i = 1; i <= numberOfDepots; i++) {
            int endIndex = depotIndexStartEnd + i - 1;
            int maxRouteDuration = dataSet.get(i).get(0);
            int maxVehicleLoad = dataSet.get(i).get(1);
            depots.add(new Depot(i, maxNumOfVehicles, maxRouteDuration, maxVehicleLoad, dataSet.get(endIndex).get(1),
                    dataSet.get(endIndex).get(2)));
        }
        return depots;
    }

    public HashMap<Integer, Customer> getCustomers() {
        return (HashMap<Integer, Customer>) dataSet.subList(numberOfDepots + 1, depotIndexStartEnd).stream()
                .collect(Collectors.toMap(customerRow -> customerRow.get(0),
                        customerRow -> new Customer(customerRow.get(0), customerRow.get(1), customerRow.get(2), customerRow.get(3))));
    }


    public int getMaxNumOfVehicles() {
        return this.maxNumOfVehicles;
    }

    public int getNumOfCustomers() {
        return this.numOfCustomers;
    }

    public int getNumberOfDepots() {
        return this.numberOfDepots;
    }

}


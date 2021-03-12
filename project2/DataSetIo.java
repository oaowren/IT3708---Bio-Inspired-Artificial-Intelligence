import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import DataClasses.Tuple;
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
                              .map(Integer::parseInt)
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
        lines.add(String.format("%.2f", Fitness.getIndividualRouteFitness(individual)));
        for (Depot d: depots){
            for (Vehicle v: d.getAllVehicles()){
                if (v.isActive()){
                    lines.add(String.format("%d\t%d\t%.2f\t%d\t%d %s %d", d.id, v.id, Fitness.getVehicleFitness(v, d), v.getLoad(), 0, v.getCustomerSequence(), 0));
                }
            }
        }
        try {
            Files.write(Paths.get(filename), lines);
        } catch (IOException error) {
            System.out.println(error.toString());
        }
    }

    public static void writeGenerations(List<Tuple<Integer, Double>> generationalFitness, String filename){
        List<String> lines = generationalFitness.stream().map(gf->String.format("%d\t%.2f", gf.x, gf.y)).collect(Collectors.toList());
        try{
            Files.write(Paths.get(filename), lines);
        } catch (IOException error){
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
                        customerRow -> new Customer(customerRow.get(0), customerRow.get(1), customerRow.get(2), customerRow.get(4))));
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


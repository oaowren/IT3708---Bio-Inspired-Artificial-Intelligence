import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import DataClasses.Customer;

public class Population {
    
    private List<Individual> individuals = new ArrayList<>();
    private int maxNumOfVehicles;
    private Fitness fitnessfunc;

    public static List<Depot> depots;
    public static HashMap<Integer, Customer> customers;

    public static void setDepots(List<Depot> newDepots) {
        depots = newDepots;
    }
    public static void setCustomers(HashMap<Integer, Customer> newCustomers) {
        customers = newCustomers;
    }
    
    public Population(int maxNumOfVehicles, Fitness fitnessfunc) {
        this.maxNumOfVehicles = maxNumOfVehicles;
        this.fitnessfunc = fitnessfunc;
    }

    public List<Individual> getIndividuals(){
        return this.individuals;
    }

    public void generatePopulation() {
        Individual newIndividual = null;
        for (int i = 0; i < Parameters.populationSize; i++) {
            newIndividual = new Individual(depots, this.maxNumOfVehicles, fitnessfunc);
            newIndividual.createRandomIndividual(customers);
            this.individuals.add(newIndividual);
        }
    }

}

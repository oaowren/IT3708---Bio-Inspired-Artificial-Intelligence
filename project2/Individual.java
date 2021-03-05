import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Random;
import DataClasses.Tuple;

public class Individual{

    private List<Depot> depots;
    private int maxVehicles;
    private double fitness;

    public Individual(List<Depot> depots, int maxVehicles) {
        this.maxVehicles = maxVehicles;
        this.depots = this.createDepots(depots);
    }

    public void calculateFitness(){
        this.fitness = Fitness.getIndividualFitness(this);
    }
    
    public List<Depot> getDepots() {
        return this.depots;
    }

    public double getFitness(){
        return this.fitness;
    }

    public List<Depot> createDepots(List<Depot> depots){
        List<Depot> depotResults = new ArrayList<>();
        for (Depot d: depots){
            Depot depotCopy = d.clone();
            for (int i = 0;i < this.maxVehicles ;i++){
                Vehicle v = new Vehicle(i+1, d.maxLoad);
                try{
                    depotCopy.addVehicle(v);
                } catch (IllegalStateException e){
                    ;
                }
            }
            depotResults.add(depotCopy);
        }
        return depotResults;
    }

    public int numberOfCustomers(){
        /* Used for debugging :):) */
        int cust = 0;
        for (Depot d: this.depots){
            for (Vehicle v: d.getAllVehicles()){
                cust += v.getCustomers().size();
            }
        }
        return cust;
    }

    public void createRandomIndividual(HashMap<Integer, Customer> customers){
        List<Customer> customerValues = new ArrayList<>(customers.values());
        Collections.shuffle(customerValues);
        for (Customer c: customerValues){
            List<Depot> validDepots = depots.stream()
                                            .filter((d) -> c.candidateList.contains(d.id))
                                            .collect(Collectors.toList());
            for (Depot depot : validDepots) {
                boolean notAssigned = true;
                int vehicleId = depot.getAllVehicles().size();
                while (notAssigned) {
                    try {
                        depot.getVehicleById(vehicleId).visitCustomer(c);
                        notAssigned = false;
                    } catch(IllegalStateException e) {
                        vehicleId--;
                        if (vehicleId <= 0) break;
                    }
                }
                if (!notAssigned) {
                    break;
                }
            }
        }
        this.calculateFitness();
    }

    public boolean removeCustomer(Customer c){
        for (Depot d: this.depots){
            boolean removed = d.removeCustomer(c);
            if (removed){
                return true;
            }
        }
        return false;
    }

    public Tuple<Individual, Individual> crossover(Individual i){
        Individual offspring1 = this.clone();
        Individual offspring2 = i.clone();
        Random rand = new Random();
        // Select a random depot for each offspring
        Depot depot1 = offspring1.getDepots().get(rand.nextInt(offspring1.getDepots().size()));
        Depot depot2 = offspring2.getDepots().get(rand.nextInt(offspring2.getDepots().size()));
        // Select a random route for each depot, empty routes can not be selected
        Vehicle vehicle1 = depot1.getAllVehicles().get(rand.nextInt(depot1.getAllVehicles().size()));
        Vehicle vehicle2 = depot2.getAllVehicles().get(rand.nextInt(depot2.getAllVehicles().size()));
        // Remove customers from opposite route, insert new at most feasible location
        List<Customer> c1 = new ArrayList<>(vehicle1.getCustomers());
        List<Customer> c2 = new ArrayList<>(vehicle2.getCustomers());
        for (Customer c: c1){
            boolean removed = offspring2.removeCustomer(c);
            if (!removed){
                return null;
            }
        }
        for (Customer c: c2){
            boolean removed = offspring1.removeCustomer(c);
            if (!removed){
                return null;
            }
        }
        for (Customer c: c1){
            boolean inserted = depot2.insertAtMostFeasible(c);
            if (!inserted){
                return null;
            }
        }
        for (Customer c: c2){
            boolean inserted = depot1.insertAtMostFeasible(c);
            if (!inserted){
                return null;
            }
        }
        offspring1.calculateFitness();
        offspring2.calculateFitness();
        return new Tuple<>(offspring1, offspring2);
    }

    public void interDepotMutation() {
        Random rand = new Random();
        Depot randomDepot1 = getDepots().get(rand.nextInt(getDepots().size()));
        Depot randomDepot2 = getDepots().get(rand.nextInt(getDepots().size()));
        while(randomDepot1 == randomDepot2) {
            randomDepot2 = getDepots().get(rand.nextInt(getDepots().size()));
        }
        // TODO:
    }

    @Override
    public Individual clone(){
        List<Depot> depots = new ArrayList<>();
        for (Depot d:this.depots){
            depots.add(d.clone());
        }
        return new Individual(depots, this.maxVehicles);
    }

}

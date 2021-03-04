import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Random;
import DataClasses.Customer;
import DataClasses.Tuple;

public class Individual{
    private List<Depot> depots;
    private int maxVehicles;
    private double fitness;

    public Individual(List<Depot> depots, int maxVehicles) {
        this.maxVehicles = maxVehicles;
        this.depots = this.createDepots(depots);
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
            int vcount = depotCopy.getAllVehicles().size();
            for (int i = 0;i < this.maxVehicles - vcount ;i++){
                Vehicle v = new Vehicle(i, d.maxLoad);
                depotCopy.addVehicle(v);
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
        Set<Integer> customerIds = customers.keySet();
        Random rand = new Random();
        for (int c: customerIds){
            boolean success = false;
            while (!success){
                try{
                    Depot depot = this.depots.get(rand.nextInt(this.depots.size()));
                    depot.getVehicleById(rand.nextInt(depot.getAllVehicles().size())).visitCustomer(customers.get(c));
                    success = true;
                } catch (IllegalStateException e) {
                    success = false;
                }
            }
        }
        this.fitness = Fitness.getIndividualFitness(this);
    }

    public boolean removeCustomerById(int id){
        for (Depot d: this.depots){
            boolean removed = d.removeCustomerById(id);
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
        // Select a random route for each depot
        Vehicle vehicle1 = depot1.getAllVehicles().get(rand.nextInt(depot1.getAllVehicles().size()));
        Vehicle vehicle2 = depot2.getAllVehicles().get(rand.nextInt(depot2.getAllVehicles().size()));
        // Remove customers from opposite route, insert new at most feasible location
        List<Customer> c1 = vehicle1.getCustomers();
        List<Customer> c2 = vehicle2.getCustomers();
        for (Customer c: c1){
            boolean removed = offspring2.removeCustomerById(c.id);
            if (!removed){
                return null;
            }
        }
        for (Customer c: c2){
            boolean removed = offspring1.removeCustomerById(c.id);
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
        return new Tuple<>(offspring1, offspring2);
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

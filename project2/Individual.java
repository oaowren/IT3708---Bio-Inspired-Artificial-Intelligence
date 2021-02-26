import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Random;
import DataClasses.Customer;

public class Individual {
    private List<Depot> depots;    
    private int maxVehicles;
    private double fitness;
    private Fitness fitnessfunc; 

    public Individual(List<Depot> depots, int maxVehicles, Fitness fitnessfunc) {
        this.depots = depots;
        this.maxVehicles = maxVehicles;
        this.fitnessfunc = fitnessfunc;
    }
    
    public List<Depot> getDepots() {
        return this.depots;
    }

    public double getFitness(){
        return this.fitness;
    }

    public void createDepots(List<Depot> depots){
        for (Depot d: depots){
            for (int i = 0;i<this.maxVehicles;i++){
                Vehicle v = new Vehicle(i, d.maxLoad);
                d.addVehicle(v);
            }
        }
        this.depots = depots;
    }

    public void createRandomIndividual(HashMap<Integer, Customer> customers){
        Set<Integer> customerIds = customers.keySet();
        Random rand = new Random();
        for (int c: customerIds){
            boolean success = false;
            while (!success){
                try{
                    Depot depot = this.depots.get(rand.nextInt(this.depots.size()));
                    depot.getVehicleById(rand.nextInt(depot.getAllVehicles().size())).visitCustomer(c, customers.get(c).demand);
                    success = true;
                } catch (IllegalStateException e) {
                    success = false;
                }
            }
        }
        this.fitness = this.fitnessfunc.getIndividualFitness(this);
    }

}

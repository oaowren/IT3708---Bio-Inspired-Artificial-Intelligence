import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Random;
import DataClasses.Customer;
import DataClasses.Tuple;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Individual implements Serializable {
    private static final long serialVersionUID = 971312240056082661L;
    private List<Depot> depots;
    private int maxVehicles;
    private double fitness;
    private Fitness fitnessfunc; 

    public Individual(List<Depot> depots, int maxVehicles, Fitness fitnessfunc) {
        this.maxVehicles = maxVehicles;
        this.fitnessfunc = fitnessfunc;
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
            Depot depotCopy = (Depot) deepCopy(d);
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
        this.fitness = this.fitnessfunc.getIndividualFitness(this);
    }

    public void removeCustomerById(int id){
        for (Depot d: this.depots){
            boolean removed = d.removeCustomerById(id);
            if (removed){
                return;
            }
        }
    }

    public Tuple<Individual, Individual> crossover(Individual i){
        Individual offspring1 = (Individual) deepCopy(this);
        Individual offspring2 = (Individual) deepCopy(i);
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
            offspring2.removeCustomerById(c.id);
        }
        for (Customer c: c2){
            offspring1.removeCustomerById(c.id);
        }
        for (Customer c: c1){
            boolean inserted = depot2.insertAtMostFeasible(c, this.fitnessfunc);
            if (!inserted){
                return null;
            }
        }
        for (Customer c: c2){
            boolean inserted = depot1.insertAtMostFeasible(c, this.fitnessfunc);
            if (!inserted){
                return null;
            }
        }

        return new Tuple<>(offspring1, offspring2);
    }

    private static Object deepCopy(Object object) {
        try {
          ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
          ObjectOutputStream outputStrm = new ObjectOutputStream(outputStream);
          outputStrm.writeObject(object);
          ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
          ObjectInputStream objInputStream = new ObjectInputStream(inputStream);
          return objInputStream.readObject();
        }
        catch (Exception e) {
          e.printStackTrace();
          return null;
        }
      }

}

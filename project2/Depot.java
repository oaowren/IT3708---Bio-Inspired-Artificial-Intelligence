import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import DataClasses.Tuple;

public class Depot{

    public final int id, maxLoad, maxVehicles, maxDuration, x, y;
    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Customer> swappableCustomers = new ArrayList<>();
    
    public Depot(int id, int maxVehicles, int maxDuration, int maxLoad, int x, int y){
        this.id = id;
        this.maxLoad = maxLoad;
        this.maxDuration = maxDuration;
        this.maxVehicles = maxVehicles;
        this.x = x;
        this.y = y;
    }

    public Depot(Depot depot, List<Vehicle> vehicles){
        this.id = depot.id;
        this.maxLoad = depot.maxLoad;
        this.maxDuration = depot.maxDuration;
        this.maxVehicles = depot.maxVehicles;
        this.x = depot.x;
        this.y = depot.y;
        this.swappableCustomers = depot.getSwappableCustomers();
        this.vehicles = vehicles;
    }

    public Vehicle getVehicleById(int id){
        for (Vehicle v: this.vehicles){
            if (v.id == id){
                return v;
            }
        }
        return null;
    }

    public boolean removeCustomer(Customer c){
        for (Vehicle v: this.vehicles){
            boolean removed = v.removeCustomer(c);
            if (removed){
                return true;
            }
        }
        return false;
    }

    public void removeVehicleById(int id){
        this.vehicles = this.vehicles.stream().filter(v->v.id != id).collect(Collectors.toList());
    }

    public boolean insertAtMostFeasible(Customer customer){
        Vehicle vehicle = null;
        int maxFeasible = -1;
        double minFitness = Integer.MAX_VALUE;
        for (int i=0; i<this.vehicles.size(); i++){
            Depot depotClone = this.clone();
            Vehicle v = depotClone.getAllVehicles().get(i);
            Tuple<Integer, Double> best = v.mostFeasibleInsertion(customer);
            if (best != null){
                v.insertCustomer(customer, best.x);
                Double newFitness = Fitness.getDepotFitness(depotClone);
                if (newFitness < minFitness){
                    vehicle = this.vehicles.get(i);
                    minFitness = newFitness;
                    maxFeasible = best.x;            
                }
            }
        }
        if (maxFeasible == -1){
            return false;
        }
        vehicle.insertCustomer(customer, maxFeasible);
        return true;
    }

    public List<Vehicle> getAllVehicles(){
        return this.vehicles;
    }

    public void addVehicle(Vehicle v){
        if (this.vehicles.size() >= this.maxVehicles){
            throw new IllegalStateException("Too many vehicles");
        }
        v.setDepot(this);
        this.vehicles.add(v);
    }

    public List<String> getVehicleRoutes() {
        return this.getAllVehicles().stream()
                                    .map(Vehicle::getCustomerSequence)
                                    .collect(Collectors.toList());
    }
    
    public void intraDepotMutation() {
        Random rand = new Random();
        int randInt = rand.nextInt(2);
        if (randInt == 0) {
            reversalMutation();
        } else if (randInt == 1) {
            singleCustomerRerouting();
        } else {
            swapping();
        }
    }

    /**
     * Two cutpoints are selected in the chromosome associated with the depot, 
     * and the genetic material between these two cutpoints is reversed.
     */
    private void reversalMutation() {
        Random rand = new Random();
        List<Integer> vehicleIndices = new ArrayList<>();
        List<Customer> allCustomersFromAllVehicles = new ArrayList<>();
        /* 
        *  Iterate through all vehicles and flatten to 1D list of all customers, while tracking which
        *  vehicles should contain which customers after mutation 
        */
        for (Vehicle vehicle : vehicles) {
            allCustomersFromAllVehicles.addAll(vehicle.getCustomers());
            vehicleIndices.add(allCustomersFromAllVehicles.size()); // Keep indices for vehicles for permutation
        }
        // Select two random (inequal) cutpoints in the list of all customers.
        int cutPoint1 = rand.nextInt(allCustomersFromAllVehicles.size());
        int cutPoint2 = rand.nextInt(allCustomersFromAllVehicles.size());
        while(cutPoint1 == cutPoint2) {
            cutPoint2 = rand.nextInt(allCustomersFromAllVehicles.size());
        }
        // Swap each i-th customer from each cutpoint
        for (int i = (cutPoint1 < cutPoint2 ? cutPoint1 : cutPoint2); i < (cutPoint1 < cutPoint2 ? cutPoint2 : cutPoint1); i++) {
            Customer customer1 = allCustomersFromAllVehicles.get(i);
            Customer customer2 = allCustomersFromAllVehicles.get(cutPoint2-i);
            allCustomersFromAllVehicles.set(i, customer2);
            allCustomersFromAllVehicles.set(cutPoint2-i, customer1);
        }
        // Iterate through all vehicles and add their corresponding new customers
        List<Vehicle> mutatedVehicleList = new ArrayList<>();
        Iterator<Vehicle> vehicleIterator = vehicles.iterator();
        int prevIndex = 0;
        for (Integer index : vehicleIndices) {
            mutatedVehicleList.add(new Vehicle(vehicleIterator.next(), allCustomersFromAllVehicles.subList(prevIndex, index)));
        }
    }

    /**
     * Re-routing involves randomly selecting one customer, and removing that customer from the existing route. 
     * The customer is then inserted in the best feasible insertion location within the entire chromosome. 
     * This involves computing the total cost of insertion at every insertion locale, 
     * which finally re-inserts the customer in the most feasible location.
     */
    private void singleCustomerRerouting() {
        Random rand = new Random();
        Vehicle randVehicle = vehicles.get(rand.nextInt(vehicles.size()));
        Customer randCustomer = randVehicle.getCustomers().get(rand.nextInt(randVehicle.getCustomers().size()));

        randVehicle.removeCustomer(randCustomer);
        insertAtMostFeasible(randCustomer);
    }

    /** 
     * This simple mutation operator selects two random routes and 
     * swaps one randomly chosen customer from one route to another.
    */
    private void swapping() {
        // Generate two random numbers to pick routes
        Random rand = new Random();
        int randVehicle1 = rand.nextInt(vehicles.size());
        int randVehicle2 = rand.nextInt(vehicles.size());
        while(randVehicle1 == randVehicle2) {
            randVehicle1 = rand.nextInt(vehicles.size());
        }

        Vehicle vehicle1 = vehicles.get(randVehicle1);
        Vehicle vehicle2 = vehicles.get(randVehicle2);

        // Generate two random numbers to pick customers to swap
        int randCustomer1 = rand.nextInt(vehicle1.getCustomers().size());
        int randCustomer2 = rand.nextInt(vehicle2.getCustomers().size());
        while(randCustomer1 == randCustomer2) {
            randCustomer2 = rand.nextInt(vehicle2.getCustomers().size());
        }

        // Swap customers
        Customer customer1 = vehicle1.getCustomers().get(randCustomer1);
        Customer customer2 = vehicle2.getCustomers().get(randCustomer2);
        vehicle1.getCustomers().set(randCustomer1, customer2);
        vehicle2.getCustomers().set(randCustomer2, customer1);
    }

    public List<Customer> getAllCustomersInVehicles() {
        return vehicles.stream()
                       .flatMap(v -> v.getCustomers().stream())
                       .collect(Collectors.toList());
    }

    public boolean hasActiveVehicles(){
        return vehicles.stream().anyMatch(Vehicle::isActive);
    }


    public void addSwappableCustomer(Customer customer) {
        if (!swappableCustomers.contains(customer)) {
            swappableCustomers.add(customer);
        } else {
            throw new IllegalArgumentException("Customer should not be added as a swappable customer to a Depot more than once!");
        }
    }

    public List<Customer> getSwappableCustomers(){
        return this.swappableCustomers;
    }

    @Override
    public String toString() {
        return "Depot ID" + id + "\n Depot x:" + x + "\n Depot y:" + y;
    }

    @Override
    public Depot clone(){
        List<Vehicle> vehicles = new ArrayList<>();
        Depot depot = new Depot(this, vehicles);
        for (Vehicle v: this.vehicles){
            depot.addVehicle(v.clone());
        }
        return depot;
    }

}

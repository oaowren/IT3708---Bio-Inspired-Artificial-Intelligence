import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import DataClasses.Tuple;

public class Depot{

    public final int id, maxLoad, maxVehicles, maxDuration, x, y;
    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Customer> swappableCustomers = new ArrayList<>();
    
    public Depot(int id, int maxVehicles, int maxDuration, int maxLoad, int x, int y) {
        this.id = id;
        this.maxLoad = maxLoad;
        this.maxDuration = maxDuration;
        this.maxVehicles = maxVehicles;
        this.x = x;
        this.y = y;
    }

    public Depot(Depot depot, List<Vehicle> vehicles) {
        this.id = depot.id;
        this.maxLoad = depot.maxLoad;
        this.maxDuration = depot.maxDuration;
        this.maxVehicles = depot.maxVehicles;
        this.x = depot.x;
        this.y = depot.y;
        this.swappableCustomers = depot.getSwappableCustomers();
        this.vehicles = vehicles;
    }

    public Vehicle getVehicleById(int id) {
        return vehicles.stream()
                       .filter(v -> v.id == id)
                       .findAny()
                       .orElse(null);
    }

    public boolean removeCustomer(Customer c) {
        return vehicles.stream()
                       .anyMatch(vehicle -> vehicle.removeCustomer(c));
    }

    public void removeVehicleById(int id) {
        vehicles = vehicles.stream()
                           .filter(v -> v.id != id)
                           .collect(Collectors.toList());
    }

    public boolean insertAtMostFeasible(Customer customer) {
        Vehicle vehicle = null;
        int maxFeasible = -1;
        double minFitness = Integer.MAX_VALUE;
        for (int i=0; i<this.vehicles.size(); i++) {
            Depot depotClone = this.clone();
            Vehicle v = depotClone.getAllVehicles().get(i);
            Tuple<Integer, Double> best = v.mostFeasibleInsertion(customer);
            if (best != null) {
                v.insertCustomer(customer, best.x);
                Double newFitness = Fitness.getDepotFitness(depotClone);
                if (newFitness < minFitness) {
                    vehicle = this.vehicles.get(i);
                    minFitness = newFitness;
                    maxFeasible = best.x;            
                }
            }
        }
        if (maxFeasible == -1) {
            return false;
        }
        vehicle.insertCustomer(customer, maxFeasible);
        return true;
    }

    public List<Vehicle> getAllVehicles() {
        return this.vehicles;
    }

    public void addVehicle(Vehicle v) {
        if (this.vehicles.size() >= this.maxVehicles) {
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
        int randInt = rand.nextInt(3);
        if (randInt == 0) {
            boolean mutationSuccessful = false;
            int tries = 0;
            while (!mutationSuccessful && tries < 10) {
                try {
                    reversalMutation();
                    mutationSuccessful = true;
                } catch (IllegalStateException e) {
                    tries++;
                }
            }
            // FIXME: MAGNUS 
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
        List<Vehicle> emptyVehicles = new ArrayList<>();
        /* 
        *  Iterate through all vehicles and flatten to 1D list of all customers, while tracking which
        *  vehicles should contain which customers after mutation 
        */
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getCustomers().size() < 1) {
                emptyVehicles.add(vehicle);
                continue;
            }
            allCustomersFromAllVehicles.addAll(vehicle.getCustomers());
            vehicleIndices.add(allCustomersFromAllVehicles.size()-1); // Keep indices for vehicles for permutation
        }
        // Select two random (inequal) cutpoints in the list of all customers.
        int cutPoint1 = rand.nextInt(allCustomersFromAllVehicles.size());
        int cutPoint2 = rand.nextInt(allCustomersFromAllVehicles.size());
        while(cutPoint1 == cutPoint2) {
            cutPoint2 = rand.nextInt(allCustomersFromAllVehicles.size());
        }
        int lowerBound = cutPoint1 < cutPoint2 ? cutPoint1 : cutPoint2;
        int upperBound = lowerBound == cutPoint1 ? cutPoint2 : cutPoint1;
        // Swap each i-th customer from each cutpoint
        for (int i = 0; i < upperBound-lowerBound; i++) {
            Customer customer1 = allCustomersFromAllVehicles.get(lowerBound+i);
            Customer customer2 = allCustomersFromAllVehicles.get(upperBound-i);
            allCustomersFromAllVehicles.set(lowerBound+i, customer2);
            allCustomersFromAllVehicles.set(upperBound-i, customer1);
        }
        // Iterate through all vehicles and add their corresponding new customers
        List<Vehicle> mutatedVehicleList = new ArrayList<>();
        Iterator<Vehicle> vehicleIterator = vehicles.iterator();
        int prevIndex = 0;
        for (Integer index : vehicleIndices) {
            mutatedVehicleList.add(new Vehicle(vehicleIterator.next(), allCustomersFromAllVehicles.subList(prevIndex, index)));
        }
        mutatedVehicleList.addAll(emptyVehicles);
        vehicles = mutatedVehicleList;
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
        while (randVehicle.getCustomers().size() < 1) {
            randVehicle = vehicles.get(rand.nextInt(vehicles.size()));
        }
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
        Vehicle randVehicle1 = vehicles.get(rand.nextInt(vehicles.size()));
        while (randVehicle1.getCustomers().size() < 1) {
            randVehicle1 = vehicles.get(rand.nextInt(vehicles.size()));
        }
        Vehicle randVehicle2 = vehicles.get(rand.nextInt(vehicles.size()));
        int tries = 0;
        while(randVehicle1 == randVehicle2 || randVehicle2.getCustomers().size() < 1) {
            if (tries >= Math.pow(vehicles.size(), 2)) {
                return; // Assume no swap mutations possible
            }
            randVehicle2 = vehicles.get(rand.nextInt(vehicles.size()));
            tries++;
        }

        // Generate two random numbers to pick customers to swap
        int randCustomer1 = rand.nextInt(randVehicle1.getCustomers().size());
        int randCustomer2 = rand.nextInt(randVehicle2.getCustomers().size());
        while(randCustomer1 == randCustomer2) {
            randCustomer2 = rand.nextInt(randVehicle2.getCustomers().size());
        }

        // Swap customers
        Customer customer1 = randVehicle1.getCustomers().get(randCustomer1);
        Customer customer2 = randVehicle2.getCustomers().get(randCustomer2);
        randVehicle1.getCustomers().set(randCustomer1, customer2);
        randVehicle2.getCustomers().set(randCustomer2, customer1);

    }

    public List<Customer> getAllCustomersInVehicles() {
        return vehicles.stream()
                       .flatMap(v -> v.getCustomers().stream())
                       .collect(Collectors.toList());
    }

    public boolean hasActiveVehicles() {
        return vehicles.stream().anyMatch(Vehicle::isActive);
    }


    public void addSwappableCustomer(Customer customer) {
        if (!swappableCustomers.contains(customer)) {
            swappableCustomers.add(customer);
        } else {
            throw new IllegalArgumentException("Customer should not be added as a swappable customer to a Depot more than once!");
        }
    }

    public List<Customer> getSwappableCustomers() {
        return this.swappableCustomers;
    }

    @Override
    public String toString() {
        return "Depot ID" + id + "\n Depot x:" + x + "\n Depot y:" + y;
    }

    @Override
    public Depot clone() {
        List<Vehicle> vehicles = new ArrayList<>();
        Depot depot = new Depot(this, vehicles);
        for (Vehicle v: this.vehicles) {
            depot.addVehicle(v.clone());
        }
        return depot;
    }

}

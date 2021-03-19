import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import DataClasses.Tuple;
import DataClasses.Customer;


public class Vehicle{
    
    public final int id, maxLoad, maxDuration;
    private int load = 0;
    private List<Customer> customers = new ArrayList<>();
    private Depot depot;

    public Vehicle(int id, int maxLoad, int maxDuration) {
        this.id = id;
        this.maxLoad = maxLoad;
        this.maxDuration = maxDuration;
    }

    public Vehicle(Vehicle vehicle){
        this.id = vehicle.id;
        this.maxLoad = vehicle.maxLoad;
        this.maxDuration = vehicle.maxDuration;
        this.load = vehicle.getLoad();
        this.customers = new ArrayList<>(vehicle.getCustomers());
        this.depot = vehicle.getDepot();
    }

    public Vehicle(Vehicle vehicle, List<Customer> customers){
        this.id = vehicle.id;
        this.maxLoad = vehicle.maxLoad;
        this.maxDuration = vehicle.maxDuration;
        this.depot = vehicle.getDepot();
        this.load = vehicle.getLoad();
        addCustomersToRoute(customers, 0);
    }

    public void visitCustomer(Customer customer){
        if (this.load + customer.demand > this.maxLoad) {
            throw new IllegalStateException("Too much load for current route");
        }
        this.load += customer.demand;
        this.customers.add(customer);
    }

    public void insertCustomer(Customer customer, int index){
        if (this.load + customer.demand > this.maxLoad) {
            throw new IllegalStateException("Too much load for current route");
        }
        this.load += customer.demand;
        this.customers.add(index, customer);
    }

    public void addCustomersToRoute(List<Customer> customers, int index){
        int totalDemand = customers.stream().map(c-> c.demand).reduce(0, (total, demand) -> total + demand);
        if (this.load + totalDemand > this.maxLoad) {
            throw new IllegalStateException("Too much load for current route");
        }
        this.load += totalDemand;
        this.customers.addAll(index, customers);
    }

    public boolean removeCustomer(Customer customer){
        boolean removed = this.customers.remove(customer);
        if (removed){
            this.load -= customer.demand;
            return true;
        }
        return false;
    }

    public List<Customer> removeCustomersFromRoute(int startIndex, int endIndex){
        List<Customer> removedCustomers = new ArrayList<>();
        removedCustomers.addAll(this.customers.subList(startIndex, endIndex));
        this.customers.removeAll(removedCustomers);
        int totalDemand = removedCustomers.stream()
                                          .map(c->c.demand)
                                          .reduce(0, (total, demand) -> total+demand);
        this.load -= totalDemand;
        return removedCustomers;
    }

    public Integer mostFeasibleInsertion(Customer customer){
        if (this.load + customer.demand > this.maxLoad){
            return null;
        }
        int lengthOfRoute = this.customers.size();
        if (lengthOfRoute == 0){
            return 0;
        }
        double lowestDiff = Integer.MAX_VALUE;
        int index = -1;
        for (int i=0; i< lengthOfRoute + 1;i++){
            double currentDist;
            double diff;
            if (i==0){
                currentDist = Fitness.getDistance(this.customers.get(0), this.depot);
                diff = Fitness.getDistance(customer, this.depot) + Fitness.getDistance(customer, this.customers.get(0));
            } else if(i == lengthOfRoute) {
                currentDist = Fitness.getDistance(this.customers.get(lengthOfRoute-1), this.depot);
                diff = Fitness.getDistance(customer, this.depot) + Fitness.getDistance(customer, this.customers.get(lengthOfRoute-1));
            } else {
                currentDist = Fitness.getDistance(this.customers.get(i-1), this.customers.get(i));
                diff = Fitness.getDistance(customer, this.customers.get(i-1)) + Fitness.getDistance(customer, this.customers.get(i)) - currentDist;
            }
            if (diff < lowestDiff){
                lowestDiff = diff;
                index = i;
            }
        }
        if (index == -1){
            return null;
        }
        return index;
    }

    public void setDepot(Depot depot){
        this.depot = depot;
    }

    public Depot getDepot(){
        return this.depot;
    }

    public String getCustomerSequence(){
        return customers.stream()
                        .map(customer -> Integer.toString(customer.id))
                        .reduce("", (output, c) -> output + (output.matches("")? "":" ") + c);
    }

    public List<Integer> getCustomersId(){
        return customers.stream()
                        .map(customer -> customer.id)
                        .collect(Collectors.toList());
    }

    public List<Customer> getCustomers(){
        return this.customers;
    }

    public boolean isActive() {
        return this.customers.size() > 0;
    }

    public int getLoad(){
        return this.load;
    }


    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Vehicle)) {
            return false;
        }
        Vehicle vehicle = (Vehicle) o;
        return id == vehicle.id 
            && maxLoad == vehicle.maxLoad 
            && load == vehicle.load 
            && Objects.equals(customers, vehicle.customers);
    }

    @Override
    public Vehicle clone(){
        return new Vehicle(this);
    }
}

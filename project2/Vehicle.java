import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.ArrayList;

import DataClasses.Customer;
import DataClasses.Tuple;


public class Vehicle{
    
    public final int id, maxLoad;
    private int load = 0;
    private List<Customer> customers = new ArrayList<>();
    private Depot depot;

    public Vehicle(int id, int maxLoad) {
        this.id = id;
        this.maxLoad = maxLoad;
    }

    public Vehicle(Vehicle vehicle){
        this.id = vehicle.id;
        this.maxLoad = vehicle.maxLoad;
        this.load = vehicle.getLoad();
        this.customers = new ArrayList<>(vehicle.getCustomers());
        this.depot = vehicle.getDepot();
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
        try{
            this.customers.remove(customer);
            this.load -= customer.demand;
            return true;
        } catch (IndexOutOfBoundsException e){
            return false;
        }
    }

    public List<Customer> removeCustomersFromRoute(int startIndex, int endIndex){
        List<Customer> removedCustomers = new ArrayList<>();
        removedCustomers.addAll(this.customers.subList(startIndex, endIndex));
        this.customers.removeAll(removedCustomers);
        int totalDemand = removedCustomers.stream().map(c->c.demand).reduce(0, (total, demand) -> total+demand);
        this.load -= totalDemand;
        return removedCustomers;
    }

    public Tuple<Integer, Double> mostFeasibleInsertion(Customer customer, Fitness f){
        int lengthOfRoute = this.customers.size();
        List<Tuple<Integer, Double>> indexAndFitness = new ArrayList<>();
        for (int i=0; i< lengthOfRoute + 1;i++){
            Vehicle copy = this.clone();
            try{
                copy.insertCustomer(customer, i);
                indexAndFitness.add(new Tuple<>(i, f.getVehicleFitness(copy, copy.depot)));
            } catch (Exception e){
                ;
            }
        }
        indexAndFitness.sort((a,b) -> a.y > b.y ? 1 : -1);
        try{
            return indexAndFitness.get(0);
        } catch (IndexOutOfBoundsException e){
            return null;
        }
        
    }

    public void setDepot(Depot depot){
        this.depot = depot;
    }

    public Depot getDepot(){
        return this.depot;
    }

    public String getCustomerSequence(){
        return this.customers.stream()
                            .map(c -> Integer.toString(c.id))
                            .reduce("", (output, c) -> output + (output.matches("")? "":" ") + c);
    }

    public List<Integer> getCustomersId(){
        return this.customers.stream().map(c->c.id).collect(Collectors.toList());
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
        return id == vehicle.id && maxLoad == vehicle.maxLoad && load == vehicle.load && Objects.equals(customers, vehicle.customers);
    }

    @Override
    public Vehicle clone(){
        return new Vehicle(this);
    }
}

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.ArrayList;
import DataClasses.Customer;

public class Vehicle {
    
    public final int id, maxLoad;
    private int load = 0;
    private List<Customer> customers = new ArrayList<>();
    private Depot depot;

    public Vehicle(int id, int maxLoad) {
        this.id = id;
        this.maxLoad = maxLoad;
    }

    public void visitCustomer(Customer customer){
        if (this.load + customer.demand > this.maxLoad) {
            throw new IllegalStateException("Too much load for current route");
        }
        this.load += customer.demand;
        this.customers.add(customer);
    }

    public void addCustomersToRoute(List<Customer> customers, int index){
        int totalDemand = customers.stream().map(c-> c.demand).reduce(0, (total, demand) -> total + demand);
        if (this.load + totalDemand > this.maxLoad) {
            throw new IllegalStateException("Too much load for current route");
        }
        this.load += totalDemand;
        this.customers.addAll(index, customers);
    }

    public List<Customer> removeCustomersFromRoute(int startIndex, int endIndex){
        List<Customer> removedCustomers = new ArrayList<>();
        removedCustomers.addAll(this.customers.subList(startIndex, endIndex));
        this.customers.removeAll(removedCustomers);
        int totalDemand = removedCustomers.stream().map(c->c.demand).reduce(0, (total, demand) -> total+demand);
        this.load -= totalDemand;
        return removedCustomers;
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

}

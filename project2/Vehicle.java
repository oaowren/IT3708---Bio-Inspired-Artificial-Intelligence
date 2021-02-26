import java.util.List;
import java.util.ArrayList;
import DataClasses.Customer;

public class Vehicle {
    
    public final int id, maxLoad;
    private int load = 0;
    private List<Customer> customers = new ArrayList<Customer>();
    private Depot depot;

    public Vehicle(int id, int maxLoad) {
        this.id = id;
        this.maxLoad = maxLoad;
    }

    public void setDepot(Depot depot) {
        this.depot = depot;
    }

    public void visitCustomer(Customer customer) {
        if (this.load + customer.demand > this.maxLoad) {
            throw new IllegalStateException("Too much load for current route");
        }
        this.load += customer.demand;
        this.customers.add(customer);
    }

    public String getCustomerSequence() {
        return this.customers.stream().map(c -> Integer.toString(c.id)).reduce("",
                (output, c) -> output + (output.matches("") ? "" : " ") + c);
    }

    public boolean isActive() {
        return customers.size() > 0;
    }

}

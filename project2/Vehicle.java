import java.util.List;
import java.util.Objects;
import java.util.ArrayList;

public class Vehicle {
    
    public final int id, maxLoad;
    private int load = 0;
    private List<Integer> customers = new ArrayList<Integer>();
    private Depot depot;

    public Vehicle(int id, int maxLoad) {
        this.id = id;
        this.maxLoad = maxLoad;
    }

    public void visitCustomer(int customer, int demand){
        if (this.load + demand > this.maxLoad) {
            throw new IllegalStateException("Too much load for current route");
        }
        this.load += demand;
        this.customers.add(customer);
    }

    public void setDepot(Depot depot){
        this.depot = depot;
    }

    public String getCustomerSequence(){
        return this.customers.stream()
                            .map(c -> Integer.toString(c))
                            .reduce("", (output, c) -> output + (output.matches("")? "":" ") + c);
    }

    public List<Integer> getCustomers(){
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

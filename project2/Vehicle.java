import java.util.List;
import java.util.Objects;
import java.util.ArrayList;
import DataClasses.Customer;

public class Vehicle {
    
    public final int id, maxLoad;
    private int load = 0;
    private List<Customer> route = new ArrayList<Customer>();
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
        this.route.add(customer);
    }

    public String getCustomerSequence() {
        return this.route.stream().map(c -> Integer.toString(c.id)).reduce("",
                (output, c) -> output + (output.matches("") ? "" : " ") + c);
    }

    public boolean isActive() {
        return route.size() > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Vehicle)) {
            return false;
        }
        Vehicle vehicle = (Vehicle) o;
        return id == vehicle.id && maxLoad == vehicle.maxLoad && load == vehicle.load && Objects.equals(route, vehicle.route) && Objects.equals(depot, vehicle.depot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, maxLoad, load, route, depot);
    }


}

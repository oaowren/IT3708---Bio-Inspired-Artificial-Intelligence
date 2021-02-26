import java.util.List;
import java.util.ArrayList;

public class Vehicle {
    
    public final int id, maxLoad;
    private int load = 0;
    private List<Integer> customers = new ArrayList<Integer>();

    public Vehicle(int id, int maxLoad){
        this.id = id;
        this.maxLoad = maxLoad;
    }

    public void visitCustomer(int customer, int demand){
        if (this.load + demand > this.maxLoad){
            throw new IllegalStateException("Too much load for current route");
        }
        this.load += demand;
        this.customers.add(customer);
    }

    public String getCustomerSequence(){
        return this.customers.stream()
                            .map(c -> Integer.toString(c))
                            .reduce("", (output, c) -> output + (output.matches("")? "":" ") + c);
    }

    public List<Integer> getCustomers(){
        return this.customers;
    }

}

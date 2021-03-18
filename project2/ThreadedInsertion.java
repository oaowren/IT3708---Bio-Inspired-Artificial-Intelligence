import DataClasses.Customer;
import DataClasses.Tuple;

public class ThreadedInsertion implements Runnable {
    
    private Vehicle vehicle;
    private Customer customer;
    Tuple<Integer, Double> best;

    public ThreadedInsertion(Vehicle vehicle, Customer customer) {
        this.vehicle = vehicle;
        this.customer = customer; 
    }

    public void run() {
        this.best = vehicle.mostFeasibleInsertion(customer);
    }
}

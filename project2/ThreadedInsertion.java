import java.util.List;
import java.util.Objects;

import DataClasses.Customer;
import DataClasses.Tuple;

public class ThreadedInsertion implements Runnable {
    
    private Vehicle vehicle;
    private Customer customer;
    private int id;
    List<Tuple<Integer, Tuple<Integer, Double>>> currentBest;

    public ThreadedInsertion(Vehicle vehicle, Customer customer, List<Tuple<Integer, Tuple<Integer, Double>>> currentBest, int id) {
        this.vehicle = vehicle;
        this.customer = customer; 
        this.currentBest = currentBest;
        this.id = id;
    }

    public void run() {
        Tuple<Integer, Double> best = vehicle.mostFeasibleInsertion(customer);
        synchronized (currentBest){
            if (!Objects.isNull(best)){
                currentBest.add(new Tuple<>(this.id, best));
            }
        }
    }
}

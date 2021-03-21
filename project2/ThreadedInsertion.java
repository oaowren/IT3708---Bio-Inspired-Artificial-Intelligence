import java.util.List;

import DataClasses.Customer;
import DataClasses.Tuple;

public class ThreadedInsertion implements Runnable {
    
    private Vehicle vehicle;
    private Customer customer;
    List<Tuple<Tuple<Vehicle, Integer>, Double>> feasible;

    public ThreadedInsertion(Vehicle vehicle, Customer customer, List<Tuple<Tuple<Vehicle, Integer>, Double>> feasible) {
        this.vehicle = vehicle;
        this.customer = customer; 
        this.feasible = feasible;
    }

    public void run() {
        Tuple<Integer, Double> best = vehicle.feasibleInsertion(customer);
        if (best == null){
            return;
        }
        synchronized (feasible){
            feasible.add(new Tuple<>(new Tuple<>(this.vehicle, best.x), best.y));
        }
    }
}

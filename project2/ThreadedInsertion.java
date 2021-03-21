import java.util.List;
import java.util.Objects;

import DataClasses.Customer;
import DataClasses.Tuple;

public class ThreadedInsertion implements Runnable {
    
    private Vehicle vehicle;
    private Customer customer;
    private int id;
    List<Tuple<Vehicle, Integer>> feasible, inFeasible;

    public ThreadedInsertion(Vehicle vehicle, Customer customer, List<Tuple<Vehicle, Integer>> feasible, List<Tuple<Vehicle, Integer>> inFeasible) {
        this.vehicle = vehicle;
        this.customer = customer; 
        this.feasible = feasible;
        this.inFeasible = inFeasible;
    }

    public void run() {
        Tuple<Integer, Boolean> best = vehicle.feasibleInsertion(customer);
        if (best == null) {
            return;
        }
        if (best.y){
            synchronized (feasible){
                feasible.add(new Tuple<>(this.vehicle, best.x));
            }
        } else {
            synchronized (inFeasible){
                inFeasible.add(new Tuple<>(this.vehicle, best.x));
            }
        }
    }
}

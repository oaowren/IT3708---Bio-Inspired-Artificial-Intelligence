import java.util.List;
import java.util.ArrayList;

public class Vehicle {
    
    public final int id, maxLoad;
    private int load = 0;
    private List<Customer> customers = new ArrayList<Customer>();
    private Depot depot;

    public Vehicle(int id, int maxLoad){
        this.id = id;
        this.maxLoad = maxLoad;
    }

    public void setDepot(Depot depot){
        this.depot = depot;
    }

    public void visitCustomer(Customer customer){
        if (this.load + customer.demand > this.maxLoad){
            throw new IllegalStateException("Too much load for current route");
        }
        this.load += customer.demand;
        this.customers.add(customer);
    }

    public String getCustomerSequence(){
        return this.customers.stream()
                            .map(c -> Integer.toString(c.id))
                            .reduce("", (output, c) -> output + (output.matches("")? "":" ") + c);
    }

    public double getTotalRouteDistance(){
        double distance = 0.0;
        int final_ind = this.customers.size()-1;
        distance += this.getDistance(this.depot.x, this.customers.get(0).x, this.depot.y, this.customers.get(0).y);
        for (int i = 0;i<final_ind;i++){
            distance += this.getDistance(this.customers.get(i).x, this.customers.get(i+1).x, this.customers.get(i).y, this.customers.get(i+1).y);
        }
        distance += this.getDistance(this.depot.x, this.customers.get(final_ind).x, this.depot.y, this.customers.get(final_ind).y);
        return distance;
    }

    private double getDistance(int x1, int x2, int y1, int y2){
        double result = Math.sqrt(Math.pow(Math.abs(x1-x2),2) + Math.pow(Math.abs(y1-y2),2));
        return result;
    }
}

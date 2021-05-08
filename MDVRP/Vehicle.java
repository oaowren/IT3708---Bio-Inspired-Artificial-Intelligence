import java.util.List;
import java.util.Objects;
import java.util.ArrayList;

import data_classes.*;


public class Vehicle{
    
    public final int id, maxLoad, maxDuration;
    private int load = 0;
    private List<Customer> customers = new ArrayList<>();
    private Depot depot;

    public Vehicle(int id, int maxLoad, int maxDuration) {
        this.id = id;
        this.maxLoad = maxLoad;
        this.maxDuration = maxDuration;
    }

    public Vehicle(Vehicle vehicle){
        this.id = vehicle.id;
        this.maxLoad = vehicle.maxLoad;
        this.maxDuration = vehicle.maxDuration;
        this.load = vehicle.getLoad();
        this.customers = new ArrayList<>(vehicle.getCustomers());
        this.depot = vehicle.getDepot();
    }

    public Vehicle(Vehicle vehicle, List<Customer> customers){
        this.id = vehicle.id;
        this.maxLoad = vehicle.maxLoad;
        this.maxDuration = vehicle.maxDuration;
        this.depot = vehicle.getDepot();
        this.load = 0;
        addCustomersToRoute(customers, 0);
    }

    public void visitCustomer(Customer customer){
        if (this.load + customer.demand > this.maxLoad && Parameters.forceMaxLoad){
            throw new IllegalStateException("Too much load for current route");
        }
        this.load += customer.demand;
        this.customers.add(customer);
    }

    public void insertCustomer(Customer customer, int index){
        if (this.load + customer.demand > this.maxLoad && Parameters.forceMaxLoad) {
            throw new IllegalStateException("Too much load for current route");
        }
        this.load += customer.demand;
        this.customers.add(index, customer);
    }

    public void addCustomersToRoute(List<Customer> customers, int index){
        int totalDemand = customers.stream().map(c-> c.demand).reduce(0, (total, demand) -> total + demand);
        if (this.load + totalDemand > this.maxLoad && Parameters.forceMaxLoad) {
            throw new IllegalStateException("Too much load for current route");
        }
        this.load += totalDemand;
        this.customers.addAll(index, customers);
    }

    public boolean removeCustomer(Customer customer){
        boolean removed = this.customers.remove(customer);
        if (removed){
            this.load -= customer.demand;
            return true;
        }
        return false;
    }

    public Tuple<Integer, Double> feasibleInsertion(Customer customer){
        if (this.load + customer.demand > this.maxLoad && Parameters.forceMaxLoad){
            return null;
        }
        List<Customer> cCopy = new ArrayList<>(this.customers);
        int lengthOfRoute = cCopy.size();
        double lowestDiff = Integer.MAX_VALUE;
        int index = -1;
        if (lengthOfRoute == 0){
            try {
                lowestDiff = 2 * Fitness.getDistance(customer, this.depot);
            } catch (NullPointerException e){
                return null;
            }

            double durationDeviation = 0;
            if (this.maxDuration != 0){
                durationDeviation = (lowestDiff > this.maxDuration ? lowestDiff - this.maxDuration : 0);
            }
            return new Tuple<>(0, Parameters.beta * lowestDiff + Parameters.durationPenalty * durationDeviation);
        }
        for (int i=0; i< lengthOfRoute + 1 ;i++){
            double currentDist, diff;
            try {
                if (i==0){
                    currentDist = Fitness.getDistance(cCopy.get(0), this.depot);
                    diff = Fitness.getDistance(customer, this.depot) + Fitness.getDistance(customer, cCopy.get(0)) - currentDist;
                } else if(i == lengthOfRoute) {
                    currentDist = Fitness.getDistance(cCopy.get(lengthOfRoute-1), this.depot);
                    diff = Fitness.getDistance(customer, this.depot) + Fitness.getDistance(customer, cCopy.get(lengthOfRoute-1)) - currentDist;
                } else {
                    currentDist = Fitness.getDistance(cCopy.get(i-1), cCopy.get(i));
                    diff = Fitness.getDistance(customer, cCopy.get(i-1)) + Fitness.getDistance(customer, cCopy.get(i)) - currentDist;
                }
            } catch (NullPointerException e){
                return null;
            }
            if (diff < lowestDiff) {
                lowestDiff = diff;
                index = i;
            }
        }
        if (index == -1){
            return null;
        }
        double loadDeviation = 0;
        if (!Parameters.forceMaxLoad){
            // Only count contributions made by the new insertion.
            loadDeviation = this.load > this.maxLoad ? customer.demand : (customer.demand + this.load > this.maxLoad  ? this.load + customer.demand - this.maxLoad : 0);
        }
        double oldFit = Fitness.getVehicleFitness(this, this.depot);
        double durationDeviation = 0;
        if (this.maxDuration != 0){
            // Only count contributions made by the new insertion.
            durationDeviation = (oldFit > this.maxDuration ? lowestDiff : (lowestDiff + oldFit > this.maxDuration ? lowestDiff + oldFit - this.maxDuration : 0));
        }
        // Returns the index of best insertion and the new fitness-value which will give a globally optimal choice in Depot
        return new Tuple<>(index, Parameters.loadPenalty * loadDeviation + 
                                  Parameters.beta * lowestDiff + 
                                  Parameters.durationPenalty * durationDeviation);
    }

    public void setDepot(Depot depot){
        this.depot = depot;
    }

    public Depot getDepot(){
        return this.depot;
    }

    public String getCustomerSequence(){
        return customers.stream()
                        .map(customer -> Integer.toString(customer.id))
                        .reduce("", (output, c) -> output + (output.matches("")? "":" ") + c);
    }

    public List<Customer> getCustomers(){
        return this.customers;
    }

    public boolean isActive() {
        return this.customers.size() > 0;
    }

    public int getLoad(){
        return this.load;
    }


    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Vehicle)) {
            return false;
        }
        Vehicle vehicle = (Vehicle) o;
        return id == vehicle.id 
            && maxLoad == vehicle.maxLoad 
            && load == vehicle.load 
            && Objects.equals(customers, vehicle.customers);
    }

    @Override
    public Vehicle clone(){
        return new Vehicle(this);
    }
}

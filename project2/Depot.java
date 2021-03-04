import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import DataClasses.Customer;
import DataClasses.Tuple;

public class Depot{

    public final int id, maxLoad, maxVehicles, maxDuration, x, y;
    private List<Vehicle> vehicles = new ArrayList<Vehicle>();
    
    public Depot(int id, int maxVehicles, int maxDuration, int maxLoad, int x, int y){
        this.id = id;
        this.maxLoad = maxLoad;
        this.maxDuration = maxDuration;
        this.maxVehicles = maxVehicles;
        this.x = x;
        this.y = y;
    }

    public Depot(Depot depot, List<Vehicle> vehicles){
        this.id = depot.id;
        this.maxLoad = depot.maxLoad;
        this.maxDuration = depot.maxDuration;
        this.maxVehicles = depot.maxVehicles;
        this.x = depot.x;
        this.y = depot.y;
        this.vehicles = vehicles;
    }

    public Vehicle getVehicleById(int id){
        for (Vehicle v: this.vehicles){
            if (v.id == id){
                return v;
            }
        }
        return null;
    }

    public boolean removeCustomerById(int id){
        for (Vehicle v: this.vehicles){
            boolean removed = v.removeCustomerById(id);
            if (removed){
                return true;
            }
        }
        return false;
    }

    public boolean insertAtMostFeasible(Customer customer, Fitness f){
        Vehicle vehicle = null;
        int maxFeasible = -1;
        double minFitness = Integer.MAX_VALUE;
        for (int i=0; i<this.vehicles.size(); i++){
            Tuple<Integer, Double> best = this.vehicles.get(i).mostFeasibleInsertion(customer, f);
            if (best!=null && best.y < minFitness){
                vehicle = this.vehicles.get(i);
                minFitness = best.y;
                maxFeasible = best.x;                
            }
        }
        if (maxFeasible == -1){
            return false;
        }
        vehicle.insertCustomer(customer, maxFeasible);
        return true;
    }

    public List<Vehicle> getAllVehicles(){
        return this.vehicles;
    }

    public void addVehicle(Vehicle v){
        if (this.vehicles.size() >= this.maxVehicles){
            throw new IllegalStateException("Too many vehicles");
        }
        v.setDepot(this);
        this.vehicles.add(v);
    }

    public List<String> getVehicleRoutes() {
        return this.getAllVehicles().stream().map(v -> v.getCustomerSequence()).collect(Collectors.toList());
    }
    
    public void intraDepotMutation() {
        Random rand = new Random();
        int randInt = rand.nextInt(2);
        if (randInt == 0) {
            reversalMutation();
        } else if (randInt == 1) {
            singleCustomerRerouting();
        } else {
            swapping();
        }
    }

    private void reversalMutation() {
        Random rand = new Random();
        List<String> vehicleRoutes = getVehicleRoutes();
        int cutPoint1 = rand.nextInt(2);
        int cutPoint2 = rand.nextInt(2);

    }
    private void singleCustomerRerouting() {

    }
    private void swapping() {
        
    }

    @Override
    public String toString() {
        String output = "Depot ID" + id + "\n Depot x:" + x + "\n Depot y:" + y;
        return output;
    }

    @Override
    public Depot clone(){
        List<Vehicle> vehicles = new ArrayList<>();
        for (Vehicle v: this.vehicles){
            vehicles.add(v.clone());
        }
        return new Depot(this, vehicles);
    }

}

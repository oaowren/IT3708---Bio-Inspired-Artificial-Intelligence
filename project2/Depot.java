import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import DataClasses.Customer;

public class Depot {

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

    public Vehicle getVehicleById(int id){
        for (Vehicle v: this.vehicles){
            if (v.id == id){
                return v;
            }
        }
        return null;
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
        Random rand = new Random();
        Vehicle randVehicle = vehicles.get(rand.nextInt(vehicles.size()));
        Customer randCustomer = randVehicle.getCustomers().get(rand.nextInt(randVehicle.getCustomers().size()));

        // TODO
    }

    private void swapping() {
        // Generate two random numbers to pick routes
        Random rand = new Random();
        int randVehicle1 = rand.nextInt(vehicles.size());
        int randVehicle2 = rand.nextInt(vehicles.size());
        while(randVehicle1 == randVehicle2) {
            randVehicle1 = rand.nextInt(vehicles.size());
        }

        Vehicle vehicle1 = vehicles.get(randVehicle1);
        Vehicle vehicle2 = vehicles.get(randVehicle2);

        // Generate two random numbers to pick customers to swap
        int randCustomer1 = rand.nextInt(vehicle1.getCustomers().size());
        int randCustomer2 = rand.nextInt(vehicle2.getCustomers().size());
        while(randCustomer1 == randCustomer2) {
            randCustomer2 = rand.nextInt(vehicle2.getCustomers().size());
        }

        Customer customer1 = vehicle1.getCustomers().get(randCustomer1);
        Customer customer2 = vehicle2.getCustomers().get(randCustomer2);
        
        vehicle1.getCustomers().set(randCustomer1, customer2);
        vehicle2.getCustomers().set(randCustomer2, customer1);
    }

    @Override
    public String toString() {
        String output = "Depot ID" + id + "\n Depot x:" + x + "\n Depot y:" + y;
        return output;
    }

}

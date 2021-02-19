import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<String> getVehicleRoutes(){
        return this.getAllVehicles()
                    .stream()
                    .map(v -> v.getCustomerSequence())
                    .collect(Collectors.toList());
    }


}

import java.util.ArrayList;
import java.util.List;

public class Depot {

    public final int id, maxLoad, maxVehicles;
    private List<Vehicle> vehicles = new ArrayList<Vehicle>();
    
    public Depot(int id, int maxLoad, int maxVehicles){
        this.id = id;
        this.maxLoad = maxLoad;
        this.maxVehicles = maxVehicles;
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
        this.vehicles.add(v);
    }


}

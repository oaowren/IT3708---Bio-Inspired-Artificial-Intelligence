import java.util.ArrayList;
import java.util.List;

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
        this.vehicles.add(v);
    }

    @Override
    public String toString() {
        return String.format(
            """
            Depot ID: %d
            Depot x: %d
            Depot y: %d
            """, id, x, y);
    }

}

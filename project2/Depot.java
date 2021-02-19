import java.util.ArrayList;
import java.util.List;

public class Depot {

    private int id;
    private List<Vehicle> vehicles = new ArrayList<Vehicle>();
    
    public Depot(int id){
        this.id = id;
    }

    public int getId(){
        return this.id;
    }

    public Vehicle getVehicleById(int id){
        
    }


}

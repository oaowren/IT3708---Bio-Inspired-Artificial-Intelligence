import java.util.List;
import java.util.ArrayList;

class Main{
    public static void main(String[] args){
        Depot d = new Depot(1, 60, 4, 80, 40, 40);
        List<Depot> depots = new ArrayList<Depot>();
        Vehicle v = new Vehicle(1, 80);
        Vehicle v1 = new Vehicle(2, 90);
        d.addVehicle(v);
        d.addVehicle(v1);
        depots.add(d);
        Customer c = new Customer(1, 40, 40, 0, 17);
        Customer c2 = new Customer(2, 50, 50, 0, 17);
        v.visitCustomer(c.id);
        v1.visitCustomer(c2.id);
        DataSetIo.writeResults(depots, "testorama.txt");
    }
}
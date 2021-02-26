import java.util.HashMap;
import java.util.List;
import DataClasses.*;

public class Fitness {

    // Memoize distance of route from/to a given depot
    private HashMap<Tuple<String, Depot>, Double> routeMemo = new HashMap<>();
    // Memoize euclidian distance between two points
    private HashMap<Tuple<Integer, Integer>, Double> pairMemo = new HashMap<>();
    private HashMap<Integer, Customer> customers;
    
    public Fitness(HashMap<Integer, Customer> customers){
        this.customers = customers;
    }

    // Memoized in routeMemo
    public Double getVehicleFitness(Vehicle route, Depot depot){
        Tuple<String, Depot> pair = new Tuple<>(route.getCustomerSequence(), depot);
        if (this.routeMemo.containsKey(pair)){
            return this.routeMemo.get(pair);
        }
        Double distance = 0.0;
        List<Integer> routeCustomers = route.getCustomers();
        int final_ind = routeCustomers.size()-1;
        distance += this.getDistance(depot.x, this.customers.get(routeCustomers.get(0)).x, depot.y, this.customers.get(routeCustomers.get(0)).y);
        for (int i = 0;i<final_ind;i++){
            distance += this.getDistance(this.customers.get(routeCustomers.get(i)).x, this.customers.get(routeCustomers.get(i+1)).x, this.customers.get(routeCustomers.get(i)).y, this.customers.get(routeCustomers.get(i+1)).y);
        }
        distance += this.getDistance(depot.x, this.customers.get(routeCustomers.get(final_ind)).x, depot.y, this.customers.get(routeCustomers.get(0)).y);
        this.routeMemo.put(pair, distance);
        return distance;
    }

    public Double getIndividualFitness(Individual individual) {
        return individual.getDepots()
                         .stream()
                         .map(depot -> this.getDepotFitness(depot))
                         .reduce(0.0, (subtotal, depot) -> subtotal + depot);
    }

    public Double getDepotFitness(Depot depot){
        Double distance = 0.0;
        for (Vehicle v: depot.getAllVehicles()){
            distance += getVehicleFitness(v, depot);
        }
        return distance;
    }

    // Memoized in pairMemo
    private Double getDistance(int x1, int x2, int y1, int y2){
        int x = Math.abs(x1-x2);
        int y = Math.abs(y1-y2);
        Tuple<Integer, Integer> pair = new Tuple<>(x,y);
        if (this.pairMemo.containsKey(pair)){
            return this.pairMemo.get(pair);
        }
        Double result = Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
        this.pairMemo.put(pair, result);
        return result;
    }
}


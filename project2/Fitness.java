import java.util.HashMap;
import java.util.List;
import DataClasses.*;

public class Fitness{

    // Memoize distance of route from/to a given depot
    private static final HashMap<Tuple<String, Depot>, Double> routeMemo = new HashMap<>();
    // Memoize euclidian distance between two points
    private static final HashMap<Tuple<Integer, Integer>, Double> pairMemo = new HashMap<>();
    private static final HashMap<Integer, Customer> customers = new HashMap<>();
    
    public static void populateCustomers(HashMap<Integer, Customer> customers){
        customers.clear();
        customers.putAll(customers);
    }

    // Memoized in routeMemo
    public static Double getVehicleFitness(Vehicle vehicle, Depot depot){
        Tuple<String, Depot> pair = new Tuple<>(vehicle.getCustomerSequence(), depot);
        if (routeMemo.containsKey(pair)){
            return routeMemo.get(pair);
        }
        if (!vehicle.isActive()){
            return 0.0;
        }
        Double distance = 0.0;
        List<Integer> vehicleCustomers = vehicle.getCustomersId();
        int final_ind = vehicleCustomers.size()-1;
        distance += getDistance(depot.x, customers.get(vehicleCustomers.get(0)).x, depot.y, customers.get(vehicleCustomers.get(0)).y);
        for (int i = 0;i<final_ind;i++){
            distance += getDistance(customers.get(vehicleCustomers.get(i)).x, customers.get(vehicleCustomers.get(i+1)).x, customers.get(vehicleCustomers.get(i)).y, customers.get(vehicleCustomers.get(i+1)).y);
        }
        distance += getDistance(depot.x, customers.get(vehicleCustomers.get(final_ind)).x, depot.y, customers.get(vehicleCustomers.get(0)).y);
        routeMemo.put(pair, distance);
        return distance;
    }

    public static Double getIndividualFitness(Individual individual) {
        int numberOfActiveVehicles = 0;
        List<Depot> depots = individual.getDepots();
        for (Depot d: depots){
            for (Vehicle v: d.getAllVehicles()){
                numberOfActiveVehicles += v.isActive()?1:0;
            }
        }
        double fitness = getIndividualRouteFitness(individual);
        return Parameters.alpha * numberOfActiveVehicles + Parameters.beta * fitness;
    }

    public static Double getIndividualRouteFitness(Individual individual){
        return individual.getDepots().stream()
                                     .map(depot -> getDepotFitness(depot))
                                     .reduce(0.0, (subtotal, depot) -> subtotal + depot);
    }

    public static Double getDepotFitness(Depot depot){
        Double distance = 0.0;
        for (Vehicle v: depot.getAllVehicles()){
            distance += getVehicleFitness(v, depot);
        }
        return distance;
    }

    // Memoized in pairMemo
    private static Double getDistance(int x1, int x2, int y1, int y2){
        int x = Math.abs(x1-x2);
        int y = Math.abs(y1-y2);
        Tuple<Integer, Integer> pair = new Tuple<>(x,y);
        if (pairMemo.containsKey(pair)){
            return pairMemo.get(pair);
        }
        Double result = Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
        pairMemo.put(pair, result);
        return result;
    }
}


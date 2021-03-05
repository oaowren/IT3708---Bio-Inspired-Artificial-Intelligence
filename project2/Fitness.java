import java.util.HashMap;
import java.util.List;
import DataClasses.*;

public class Fitness{

    // Memoize distance of route from/to a given depot
    private static final HashMap<Tuple<String, Depot>, Double> routeMemo = new HashMap<>();
    // Memoize euclidian distance between two points
    private static final HashMap<Tuple<Integer, Integer>, Double> pairMemo = new HashMap<>();
    private static final HashMap<Integer, Customer> customers = new HashMap<>();
    
    public static void populateCustomers(HashMap<Integer, Customer> newCustomers){
        customers.clear();
        customers.putAll(newCustomers);
    }

    // Memoized in routeMemo
    public static double getVehicleFitness(Vehicle vehicle, Depot depot){
        Tuple<String, Depot> pair = new Tuple<>(vehicle.getCustomerSequence(), depot);
        if (routeMemo.containsKey(pair)){
            return routeMemo.get(pair);
        }
        if (!vehicle.isActive()){
            return 0.0;
        }
        double distance = 0.0;
        List<Integer> vehicleCustomers = vehicle.getCustomersId();
        int final_ind = vehicleCustomers.size()-1;
        distance += getDistance(customers.get(vehicleCustomers.get(0)), depot);
        for (int i = 0;i<final_ind;i++){
            distance += getDistance(customers.get(vehicleCustomers.get(i)), customers.get(vehicleCustomers.get(i+1)));
        }
        distance += getDistance(customers.get(vehicleCustomers.get(final_ind)), depot);
        routeMemo.put(pair, distance);
        return distance;
    }

    public static double getIndividualFitness(Individual individual) {
        List<Depot> depots = individual.getDepots();
        int numberOfActiveVehicles = depots.stream()
                                           .flatMap(depot -> depot.getAllVehicles().stream())
                                           .map(vehicle -> vehicle.isActive() ? 1 : 0)
                                           .reduce(0, (subtotal, element) -> subtotal + element);
        double fitness = getIndividualRouteFitness(individual);
        return Parameters.alpha * numberOfActiveVehicles + Parameters.beta * fitness;
    }

    public static double getIndividualRouteFitness(Individual individual){
        return individual.getDepots().stream()
                                     .map(Fitness::getDepotFitness)
                                     .reduce(0.0, (subtotal, element) -> subtotal + element);
    }

    public static double getDepotFitness(Depot depot){
        return depot.getAllVehicles().stream()
                                     .map(vehicle -> getVehicleFitness(vehicle, depot))
                                     .reduce(0.0, (subtotal, element) -> subtotal + element);
    }

    // Memoized in pairMemo
    public static Double getDistance(int x1, int x2, int y1, int y2){
        int x = Math.abs(x1-x2);
        int y = Math.abs(y1-y2);
        Tuple<Integer, Integer> pair = new Tuple<>(x,y);
        if (pairMemo.containsKey(pair)){
            return pairMemo.get(pair);
        }
        double result = Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
        pairMemo.put(pair, result);
        return result;
    }
    
    public static double getDistance(Customer customer1, Customer customer2) {
        return getDistance(customer1.x, customer2.x, customer1.y, customer2.y);
    }

    public static double getDistance(Customer customer, Depot depot) {
        return getDistance(customer.x, depot.x, customer.y, depot.y);
    }

    public static double getDistance(Depot depot1, Depot depot2) {
        return getDistance(depot1.x, depot2.x, depot1.y, depot2.y);
    }
}


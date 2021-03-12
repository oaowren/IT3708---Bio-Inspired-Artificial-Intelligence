import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import DataClasses.Customer;

public class GeneticAlgorithm {
    
    public static void initialDepotClustering(List<Depot> depots, Collection<Customer> customers) {
        HashMap<Integer, Depot> depotMap = new HashMap<>();
        for (Customer c : customers) {
            Depot closestDepot = depots.get(0);
            double closestDistance = Fitness.getDistance(c.x, closestDepot.x, c.y, closestDepot.y);
            for (int i = 1; i < depots.size(); i++) {
                Double fitness = Fitness.getDistance(c.x, depots.get(i).x, c.y, depots.get(i).y);
                if (fitness < closestDistance) {
                    closestDepot = depots.get(i);
                    closestDistance = Fitness.getDistance(c.x,closestDepot.x, c.y, closestDepot.y);
                }
            }
            c.candidateList.add(closestDepot.id);
            depotMap.put(closestDepot.id, closestDepot);
            closestDepot.addSwappableCustomer(c);
            for (int i = 0; i < depots.size(); i++) {
                if (depots.get(i) == closestDepot) {
                    continue;
                }
                double distance_c_d = Fitness.getDistance(c.x, c.y, depots.get(i).x, depots.get(i).y);
                // "Using Genetic Algorithms for Multi-depot Vehicle Routing" p. 90:
                if (((distance_c_d - closestDistance)/closestDistance) <= Parameters.swappableCustomerDistance) {
                    c.candidateList.add(depots.get(i).id);
                    depots.get(i).addSwappableCustomer(c);
                    depotMap.put(depots.get(i).id, depots.get(i));
                }
            }
            c.candidateList.sort((d1, d2) -> (int) (Fitness.getDistance(c, depotMap.get(d1)) - Fitness.getDistance(c, depotMap.get(d2))));
        }
    }
}

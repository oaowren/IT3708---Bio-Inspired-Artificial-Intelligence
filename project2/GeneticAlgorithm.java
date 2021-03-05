import java.util.Collection;
import java.util.List;

public class GeneticAlgorithm {
    
    public static void initialDepotClustering(List<Depot> depots, Collection<Customer> customers) {
        for (Customer c : customers) {
            Depot closestDepot = depots.get(0);
            double closestDistance = Fitness.getDistance(c.x, c.y, closestDepot.x, closestDepot.y);
            for (int i = 1; i < depots.size(); i++) {
                if (Fitness.getDistance(c.x, c.y, depots.get(i).x, depots.get(i).y) < closestDistance) {
                    closestDepot = depots.get(i);
                    closestDistance = Fitness.getDistance(c.x, c.y, closestDepot.x, closestDepot.y);
                }
            }
            c.candidateList.add(closestDepot);
            closestDepot.addSwappableCustomer(c);
            for (int i = 1; i < depots.size(); i++) {
                if (depots.get(i) == closestDepot) {
                    continue;
                }
                double distance_c_d = Fitness.getDistance(c.x, c.y, depots.get(i).x, depots.get(i).y);
                // "Using Genetic Algorithms for Multi-depot Vehicle Routing" p. 90:
                if (((distance_c_d - closestDistance)/closestDistance) <= Parameters.swappableCustomerDistance) {
                    c.candidateList.add(depots.get(i));
                    closestDepot.addSwappableCustomer(c);
                }
            }
        }
    }
}

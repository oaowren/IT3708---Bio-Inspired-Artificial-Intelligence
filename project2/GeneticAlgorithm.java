import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import DataClasses.Customer;
import DataClasses.Tuple;

public class GeneticAlgorithm {

    private int generation = 0;
    private double threshold;
    Population p;
    private List<Tuple<Integer, Double>> generationalFitness = new ArrayList<>();
    
    public GeneticAlgorithm(DataSetIo dataSet) {
        this.threshold = DataSetIo.getThreshold(Parameters.problem);
        HashMap<Integer, Customer> customers = dataSet.getCustomers();
        Fitness.populateCustomers(customers);
        List<Depot> depots = dataSet.getDepots();
        p = new Population(dataSet.getMaxNumOfVehicles());
        p.setCustomers(customers);
        p.setDepots(depots);
        initialDepotClustering(depots, customers.values());
        p.generatePopulation();
        generationalFitness.add(new Tuple<>(0, Fitness.getIndividualRouteFitness(p.getIndividualByRank(0))));
    }

    public Population getPopulation() {
        return p;
    }

    public List<Tuple<Integer, Double>> getGenerationalFitness() {
        return generationalFitness;
    }

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

    public void run() {
        Individual bestInd = p.getIndividualByRank(0);
        double bestIndFitness = Fitness.getIndividualRouteFitness(bestInd);
        while ((bestIndFitness > threshold || bestInd.getDistanceDeviation() != 0.0) && generation < Parameters.generationSpan){
            generation++;
            List<Individual> parents = p.tournamentSelection();
            List<Individual> new_pop = p.crossover(parents, generation);
            if (!Parameters.useCrowding){
                new_pop = p.survivorSelection(p.getIndividuals(), new_pop);
            }
            p.setNewPopulation(new_pop);
            bestInd = p.getIndividualByRankAndDeviation(0, p.getIndividualsWithCorrectDuration());
            bestIndFitness = Fitness.getIndividualRouteFitness(bestInd);
            System.out.println(String.format("Generation %d\tRoute Fitness: %.3f\tDeviation from max duration: %.2f", generation, bestIndFitness, bestInd.getDistanceDeviation()));
            generationalFitness.add(new Tuple<>(generation, bestIndFitness));
            Fitness.removeOldRoutes();
        }
    }
}

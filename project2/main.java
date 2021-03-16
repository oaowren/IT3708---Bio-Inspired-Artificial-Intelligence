import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

import DataClasses.*;

class Main{
    public static void main(String[] args){
        List<Tuple<Integer, Double>> gFitness = new ArrayList<>();
        DataSetIo dataSet = new DataSetIo();
        dataSet.readDataFile("project2/Data Files/"+Parameters.problem);
        double threshold = DataSetIo.getThreshold(Parameters.problem);
        int generation = 0;
        HashMap<Integer, Customer> customers = dataSet.getCustomers();
        List<Depot> depots = dataSet.getDepots();
        Fitness.populateCustomers(customers);
        Population p = new Population(dataSet.getMaxNumOfVehicles());
        p.setCustomers(customers);
        p.setDepots(depots);
        GeneticAlgorithm.initialDepotClustering(depots, customers.values());
        p.generatePopulation();
        System.out.println(p.getIndividuals().stream()
                                             .map(Fitness::getIndividualRouteFitness)
                                             .collect(Collectors.toList()));
        Individual bestInd = p.getIndividualByRank(0);
        double bestIndFitness = Fitness.getIndividualRouteFitness(bestInd);
        gFitness.add(new Tuple<>(0, bestIndFitness));
        while ((bestIndFitness > threshold || bestInd.getDistanceDeviation() != 0.0) && generation < Parameters.generationSpan){
            generation++;
            System.out.println(generation);
            List<Individual> parents = p.tournamentSelection();
            List<Individual> new_pop = p.crossover(parents, generation);
            new_pop = p.survivorSelection(p.getIndividuals(), new_pop);
            p.setNewPopulation(new_pop);
            bestInd = p.getIndividualByRankAndDeviation(0, p.getIndividualsWithCorrectDuration());
            bestIndFitness = Fitness.getIndividualRouteFitness(bestInd);
            System.out.println("Route Fitness: "+bestIndFitness);
            System.out.println("Deviation from max duration: "+bestInd.getDistanceDeviation());
            gFitness.add(new Tuple<>(generation, bestIndFitness));
            Fitness.removeOldRoutes();
        }
        DataSetIo.writeResults(p.getIndividualByRank(0), "project2/Results/"+Parameters.problem);
        DataSetIo.writeGenerations(gFitness, "project2/Generations/"+Parameters.problem);
    }
}
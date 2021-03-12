import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Comparator;

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
        // int index = 7;
        // Individual a = Utils.select(p.getIndividuals(), index, false);
        // List<Individual> b = p.getIndividuals();
        // b.sort(Comparator.comparingDouble(Individual::getFitness));
        // System.out.println(a == b.get(index));
        double bestIndFitness = Fitness.getIndividualRouteFitness(p.getIndividualByRank(0));
        gFitness.add(new Tuple<>(0, bestIndFitness));
        while (bestIndFitness > threshold && generation < Parameters.generationSpan){
            generation++;
            System.out.println(generation);
            List<Individual> parents = p.tournamentSelection();
            List<Individual> offspring = p.crossover(parents, generation);
            List<Individual> new_pop = p.survivorSelection(parents, offspring);
            p.setNewPopulation(new_pop);
            bestIndFitness = Fitness.getIndividualRouteFitness(p.getIndividualByRank(0));
            gFitness.add(new Tuple<>(generation, bestIndFitness));
            Fitness.removeOldRoutes();
        }
        DataSetIo.writeResults(p.getIndividualByRank(0), "project2/Results/"+Parameters.problem);
        DataSetIo.writeGenerations(gFitness, "project2/Generations/"+Parameters.problem);
    }
}
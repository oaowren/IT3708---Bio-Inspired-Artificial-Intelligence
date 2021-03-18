import java.util.HashMap;
import java.util.List;
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
        Individual bestInd = p.getIndividualByRank(0);
        double bestIndFitness = Fitness.getIndividualRouteFitness(bestInd);
        gFitness.add(new Tuple<>(0, bestIndFitness));
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                DataSetIo.writeResults(p.getIndividualByRank(0), "project2/Results/"+Parameters.problem);
                DataSetIo.writeGenerations(gFitness, "project2/Generations/"+Parameters.problem);
            }
        });
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
            gFitness.add(new Tuple<>(generation, bestIndFitness));
            Fitness.removeOldRoutes();
        }
        DataSetIo.writeResults(bestInd, "project2/Results/"+Parameters.problem);
        DataSetIo.writeGenerations(gFitness, "project2/Generations/"+Parameters.problem);
    }
}
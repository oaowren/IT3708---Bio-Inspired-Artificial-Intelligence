import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.ArrayList;
import DataClasses.*;

class Main{
    public static void main(String[] args){
        List<Tuple<Integer, Double>> gFitness = new ArrayList<>();
        DataSetIo dataSet = new DataSetIo();
        dataSet.readDataFile("project2/Data Files/"+Parameters.problem);
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
        System.out.println(p.getIndividuals().stream().map(n->n.numberOfCustomers()).collect(Collectors.toList()));
        for (int i=0; i< Parameters.generationSpan; i++){
            System.out.println(i+1);
            List<Individual> parents = p.tournamentSelection();
            List<Individual> offspring = p.crossover(parents, i);
            List<Individual> new_pop = p.survivor_selection(parents, offspring);
            p.setNewPopulation(new_pop);
            gFitness.add(new Tuple<>(i+1, Fitness.getIndividualRouteFitness(p.getFittestIndividual(true))));
        }
        DataSetIo.writeResults(p.getFittestIndividual(true), "project2/Results/"+Parameters.problem);
        DataSetIo.writeGenerations(gFitness, "testorama.txt");
    }

    /*TODO:
    - Mutation (intra-depot and inter-depot)
     - Intra-depot e.g. inverse-mutation (reverse subset of route)
     - Inter-depot find example*/
}
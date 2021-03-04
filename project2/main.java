import java.util.HashMap;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import DataClasses.Customer;
import java.util.Comparator;

class Main{
    public static void main(String[] args){
        DataSetIo dataSet = new DataSetIo();
        dataSet.readDataFile("project2/Data Files/p02");
        HashMap<Integer, Customer> customers = dataSet.getCustomers();
        List<Depot> depots = dataSet.getDepots();
        Fitness.populateCustomers(customers);
        Population p = new Population(dataSet.getMaxNumOfVehicles());
        p.setCustomers(customers);
        p.setDepots(depots);
        p.generatePopulation();
        System.out.println(p.getIndividuals().stream()
                                             .map(Fitness::getIndividualRouteFitness)
                                             .collect(Collectors.toList()));
        System.out.println(p.getIndividuals().stream().map(n->n.numberOfCustomers()).collect(Collectors.toList()));
        for (int i=0; i< Parameters.generationSpan; i++){
            System.out.println(i);
            List<Individual> parents = p.tournamentSelection();
            List<Individual> offspring = p.crossover(parents);
            System.out.println(p.getIndividuals().stream()
                                                 .map(Individual::numberOfCustomers)
                                                 .collect(Collectors.toList()));
            List<Individual> new_pop = p.survivor_selection(parents, offspring);
            p.setNewPopulation(new_pop);
        }
        List<Individual> pop = p.getIndividuals();
        pop.sort(Comparator.comparingDouble(Individual::getFitness));
        DataSetIo.writeResults(pop.get(0), "testorama.txt");
    }

    /*TODO:
    - Mutation (intra-depot and inter-depot)
     - Intra-depot e.g. inverse-mutation (reverse subset of route)
     - Inter-depot find example*/
}
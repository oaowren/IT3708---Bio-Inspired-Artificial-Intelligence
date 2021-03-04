import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import DataClasses.Customer;

class Main{
    public static void main(String[] args){
        DataSetIo dataSet = new DataSetIo();
        dataSet.readDataFile("project2/Data Files/p01");
        HashMap<Integer, Customer> customers = dataSet.getCustomers();
        List<Depot> depots = dataSet.getDepots();
        Fitness f = new Fitness(customers);
        Population p = new Population(dataSet.getMaxNumOfVehicles(), f);
        p.setCustomers(customers);
        p.setDepots(depots);
        p.generatePopulation();
        System.out.println(p.getIndividuals().stream().map(ind->f.getIndividualRouteFitness(ind)).collect(Collectors.toList()));
        for (int i=0; i< Parameters.generationSpan; i++){
            System.out.println(i);
            List<Individual> parents = p.tournamentSelection();
            List<Individual> new_pop = p.crossover(parents);
            p.setNewPopulation(new_pop);
        }
        System.out.println(p.getIndividuals().stream().map(ind->f.getIndividualRouteFitness(ind)).collect(Collectors.toList()));
    }

    /*TODO:
    - Crossover
     - Between depots
      - Crossover length x from depot n and m, remove customers accordingly from the other depot
      - Insert where it can satisfy constraints
    - Mutation (intra-depot and inter-depot)
     - Intra-depot e.g. inverse-mutation (reverse subset of route)
     - Inter-depot find example
    - Survivorselection
     - Most likely elitism*/
}
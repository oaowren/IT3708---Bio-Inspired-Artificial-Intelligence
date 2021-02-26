import java.util.HashMap;
import java.util.List;
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
        List<Individual> parents = p.tournamentSelection();
        System.out.println(parents.size());
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
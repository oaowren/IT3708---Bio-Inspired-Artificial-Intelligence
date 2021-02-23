import java.util.HashMap;
import DataClasses.Customer;

class Main{
    public static void main(String[] args){
        HashMap<Integer, Customer> customers = new HashMap<>();
        Customer c1 = new Customer(10,10,50);
        Customer c2 = new Customer(20,20,60);
        customers.put(1, c1);
        customers.put(2, c2);
        Fitness f = new Fitness(customers);
        Depot d = new Depot(1, 60, 4, 80, 0, 0);
        double x = f.getVehicleFitness("1 2", d);
        System.out.println(x);
    }

    /*TODO:
    - Individual (in population)-Class with list of depots
    - Memoization
    - Random inital population
     - Initialize individuals from dataset
     - Add customers to random routes as long as constraints are satisfied until all customers have a vehicle assigned
    - Parent selection
     - Total fitness (focus on fewer cars/routes, then shorter total distance)
    - Crossover
     - Between depots
      - Crossover length x from depot n and m, remove customers accordingly from the other depot
      - Insert where it can satisfy constraints
    - Mutation (intra-depot and inter-depot)
     - Intra-depot e.g. inverse-mutation (reverse subset of route)
     - Inter-depot find example
    - Survivorselection
     - Most likely elitism
    - Total Individual fitness
     - alpha * number of routes + beta * sum of distances*/
}
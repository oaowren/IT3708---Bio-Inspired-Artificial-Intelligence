import java.util.List;
import java.util.ArrayList;

class Main{
    public static void main(String[] args){
        Depot d = new Depot(1, 60, 4, 80, 40, 40);
        List<Depot> depots = new ArrayList<Depot>();
        Vehicle v = new Vehicle(1, 80);
        Vehicle v1 = new Vehicle(2, 90);
        d.addVehicle(v);
        d.addVehicle(v1);
        depots.add(d);
        Customer c = new Customer(1, 30, 30, 0, 17);
        Customer c2 = new Customer(2, 50, 50, 0, 17);
        v.visitCustomer(c);
        v.visitCustomer(c2);
        v1.visitCustomer(c2);
        DataSetIo.writeResults(depots, "testorama.txt");
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
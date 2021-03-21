import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import DataClasses.Tuple;
import DataClasses.Customer;

public class Individual{

    private List<Depot> depots;
    private int maxVehicles;
    private double fitness;

    public Individual(List<Depot> depots, int maxVehicles) {
        this.maxVehicles = maxVehicles;
        this.createDepots(depots);
    }

    public void calculateFitness(){
        this.fitness = Fitness.getIndividualFitness(this);
    }
    
    public List<Depot> getDepots() {
        return this.depots;
    }

    public Depot getDepotById(int id){
        return this.depots.stream()
                          .filter(d-> d.id == id)
                          .findAny()
                          .orElseThrow(() -> new IllegalArgumentException("Depot not found"));
    }

    public double getFitness(){
        return this.fitness;
    }

    public void createDepots(List<Depot> depots){
        List<Depot> depotResults = new ArrayList<>();
        for (Depot d: depots){
            Depot depotCopy = d.clone();
            for (int i = 0;i < this.maxVehicles ;i++){
                Vehicle v = new Vehicle(i+1, d.maxLoad, d.maxDuration);
                try{
                    depotCopy.addVehicle(v);
                } catch (IllegalStateException e){
                    ;
                }
            }
            depotResults.add(depotCopy);
        }
        this.depots = depotResults;
    }

    public int numberOfCustomers(){
        int cust = 0;
        for (Depot d: this.depots){
            for (Vehicle v: d.getAllVehicles()){
                cust += v.getCustomers().size();
            }
        }
        return cust;
    }

    public double getDistanceDeviation(){
        return this.depots.stream()
                          .map(Depot::getDistanceDeviation)
                          .reduce(0.0, (total, element) -> total + element);
    }

    public double getLoadDeviation(){
        return this.depots.stream()
                          .map(Depot::getLoadDeviation)
                          .reduce(0.0, (total, element) -> total + element);
    }

    public boolean createRandomIndividual(HashMap<Integer, Customer> customers){
        List<Customer> customerValues = new ArrayList<>(customers.values());
        Collections.shuffle(customerValues);
        for (Customer c: customerValues){
            for (int id : c.candidateList) {
                boolean notAssigned = true;
                Depot depot = this.getDepotById(id);
                int vehicleId = depot.getAllVehicles().size();
                while (notAssigned) {
                    try {
                        depot.getVehicleById(vehicleId).visitCustomer(c);
                        notAssigned = false;
                    } catch(IllegalStateException e) {
                        vehicleId--;
                        if (vehicleId <= 0) break;
                    }
                }
                if (!notAssigned) {
                    break;
                }
            }
        }
        if(this.numberOfCustomers() != customers.values().size()){
            return false;
        }
        this.calculateFitness();
        return true;
    }

    public boolean removeCustomer(Customer c){
        for (Depot d: this.depots){
            boolean removed = d.removeCustomer(c);
            if (removed){
                return true;
            }
        }
        return false;
    }

    public Vehicle randomRoute(){
        Depot d = this.getDepots().get(Utils.randomInt(this.getDepots().size()));
        return d.getAllVehicles().get(Utils.randomInt(d.getAllVehicles().size()));
    }

    public Tuple<Individual, Individual> crossover(Individual i){
        Individual offspring1 = this.clone();
        Individual offspring2 = i.clone();
        // Select a random depot for each offspring
        int randDepotIndex = Utils.randomInt(offspring1.getDepots().size());
        Depot depot1 = offspring1.getDepots().get(randDepotIndex);
        Depot depot2 = offspring2.getDepotById(depot1.id);
        // Select a random route for each offspring
        Vehicle vehicle1 = depot1.randomRoute();
        Vehicle vehicle2 = depot2.randomRoute();
        // Remove customers from opposite route, insert new at most feasible location
        List<Customer> c1 = new ArrayList<>(vehicle1.getCustomers());
        List<Customer> c2 = new ArrayList<>(vehicle2.getCustomers());
        for (Customer c: c1){
            boolean removed = offspring2.removeCustomer(c);
            if (!removed){
                return null;
            }
        }
        for (Customer c: c2){
            boolean removed = offspring1.removeCustomer(c);
            if (!removed){
                return null;
            }
        }
        for (Customer c: c1){
            boolean inserted = depot2.insertAtMostFeasible(c);
            if (!inserted){
                return null;
            }
        }
        for (Customer c: c2){
            boolean inserted = depot1.insertAtMostFeasible(c);
            if (!inserted){
                return null;
            }
        }
        return new Tuple<>(offspring1, offspring2);
    }

    public void interDepotMutation() {
        Depot randomDepot1 = getDepots().get(Utils.randomInt(getDepots().size()));
        List<Customer> allSwappableCustomersDepot1 = Utils.allSwappableCustomers(randomDepot1);

        while (allSwappableCustomersDepot1.size() < 1) {
            randomDepot1 = getDepots().get(Utils.randomInt(getDepots().size()));
            allSwappableCustomersDepot1 = Utils.allSwappableCustomers(randomDepot1);
        }
        
        Customer randomCustomer1 = Utils.randomPick(allSwappableCustomersDepot1, (customer -> customer.candidateList.size() > 1));
        if (randomCustomer1 == null) return;

        int randomDepotId = randomDepot1.id;
        Integer randomCandidateDepotId = Utils.randomPick(randomCustomer1.candidateList, (depotId -> depotId.intValue() != randomDepotId));
        if (randomCandidateDepotId == null) return;

        Collection<Integer> testedDepotIds = new ArrayList<>();
        testedDepotIds.add(randomCandidateDepotId);
        testedDepotIds.add(randomDepotId);

        Map<Integer, Depot> depotMap = depots.stream()
                                             .collect(Collectors.toMap(depot -> depot.id, depot -> depot));
                                             
        Depot randomDepot2 = depotMap.get(randomCandidateDepotId);

        boolean mutationSuccessful = false;
        while(!mutationSuccessful) {
            randomDepot2 = depotMap.get(randomCandidateDepotId);
            mutationSuccessful = randomDepot2.insertAtMostFeasible(randomCustomer1);
            if (mutationSuccessful) {
                randomDepot1.removeCustomer(randomCustomer1);
                break;
            } else {
                testedDepotIds.add(randomCandidateDepotId);
                if (testedDepotIds.size() == randomCustomer1.candidateList.size()){
                    return;
                }
                randomCandidateDepotId = Utils.randomPick(randomCustomer1.candidateList, (depotId -> !testedDepotIds.contains(depotId.intValue())));
                if (randomCandidateDepotId == null) return;
            }
        }
    }

    @Override
    public Individual clone(){
        List<Depot> depots = new ArrayList<>();
        for (Depot d:this.depots){
            depots.add(d.clone());
        }
        return new Individual(depots, this.maxVehicles);
    }

}

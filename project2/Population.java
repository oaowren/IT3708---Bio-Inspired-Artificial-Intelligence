import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import DataClasses.*;

public class Population{

    public static ExecutorService executor = 
        new ThreadPoolExecutor(
            Parameters.threadPoolSize, 
            Parameters.threadPoolSize, 
            30, TimeUnit.SECONDS, 
            new ArrayBlockingQueue<>(Parameters.threadPoolSize),
            new ThreadPoolExecutor.CallerRunsPolicy()
        );
    
	private List<Individual> individuals = new ArrayList<>();
    private int maxNumOfVehicles;

    public static List<Depot> depots;
    public static HashMap<Integer, Customer> customers;

    public void setDepots(List<Depot> newDepots) {
        depots = newDepots;
    }
    public void setCustomers(HashMap<Integer, Customer> newCustomers) {
        customers = newCustomers;
    }
    
    public Population(int maxNumOfVehicles) {
        this.maxNumOfVehicles = maxNumOfVehicles;
    }

    public List<Individual> getIndividuals(){
        return this.individuals;
    }

    public Individual getIndividualByRank(int index){
        List<Individual> copy = new ArrayList<>(individuals);
        copy.sort((a,b) -> Double.compare(Fitness.getIndividualRouteFitness(a), Fitness.getIndividualRouteFitness(b)));
        return copy.get(index);
    }

    public Individual getIndividualByRankAndDeviation(int index, boolean correctMax){
        List<Individual> copy;
        if (!correctMax){
            copy = new ArrayList<>(individuals);
            copy.sort((a,b) -> Double.compare(Fitness.getIndividualFitness(a), Fitness.getIndividualFitness(b)));
        } else {
            copy = new ArrayList<>(this.getIndividualsWithCorrectDuration());
            copy.sort((a,b) -> Double.compare(Fitness.getIndividualRouteFitness(a), Fitness.getIndividualRouteFitness(b)));
        }
        if (copy.size() == 0){
            copy = new ArrayList<>(individuals);
            copy.sort((a,b) -> Double.compare(Fitness.getIndividualFitness(a), Fitness.getIndividualFitness(b)));
        }
        return copy.get(index);
    }

    public void generatePopulation() {
        Individual newIndividual = null;
        for (int i = 0; i < Parameters.populationSize; i++) {
            newIndividual = new Individual(depots, this.maxNumOfVehicles);
            boolean created = newIndividual.createRandomIndividual(customers);
            while (!created){
                newIndividual.createDepots(depots);
                created = newIndividual.createRandomIndividual(customers);
            }
            System.out.println("Create individual: "+ (i+1));
            this.individuals.add(newIndividual);
        }
    }

    public List<Individual> tournamentSelection(){
        List<Individual> parents = Collections.synchronizedList(new ArrayList<>());
        // Create parents list of given parentSelectionSize in parameters
        for (int i = 0; i< (Parameters.parentSelectionSize - Parameters.eliteSize); i++){
            ThreadedTournament tt = new ThreadedTournament(this.individuals, parents);
            executor.execute(tt);
        }
        while (parents.size() != Parameters.parentSelectionSize - Parameters.eliteSize){
            ;
        }
        return parents;
    }

    public List<Individual> getIndividualsWithCorrectDuration(){
        return this.individuals.stream()
                               .filter(individual -> individual.getDistanceDeviation() == 0.0)
                               .collect(Collectors.toList());
    }

    public void setNewPopulation(List<Individual> population){
        this.individuals = population;
    }

    public List<Individual> crossover(List<Individual> parents, int generationCount){
        List<Individual> new_population = Collections.synchronizedList(new ArrayList<>());
        for (int i=0; i<Parameters.populationSize;i++){
            Individual p1 = parents.get(Utils.randomInt(parents.size()));
            Individual p2 = Utils.randomPick(parents, p -> p != p1);
            ThreadedCrossover offspring = new ThreadedCrossover(p1, p2, generationCount, new_population);     
            executor.execute(offspring);
        }
        return List.of(new_population.toArray(new Individual[]{}));
    }

    public List<Individual> survivorSelection(List<Individual> parents, List<Individual> offspring){
        List<Individual> copyOffspring = new ArrayList<>(offspring);
        List<Individual> copyParents = new ArrayList<>(parents);
        copyParents.sort(Comparator.comparingDouble(Individual::getFitness));
        copyOffspring.addAll(copyParents.subList(0, Parameters.eliteSize));
        return copyOffspring;
    }

}

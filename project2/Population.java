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

    public Individual getIndividualByRankAndDeviation(int index, List<Individual> inds){
        if (inds.size()==0){
            return getIndividualByRank(index);
        }
        List<Individual> copy = new ArrayList<>(inds);
        copy.sort((a,b) -> Double.compare(Fitness.getIndividualRouteFitness(a), Fitness.getIndividualRouteFitness(b)));
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
            System.out.println("Create individual: "+i);
            this.individuals.add(newIndividual);
        }
    }

    public List<Individual> tournamentSelection(){
        List<Individual> parents = Collections.synchronizedList(new ArrayList<>());
        // Create parents list of given parentSelectionSize in parameters
        ExecutorService executor = new ThreadPoolExecutor(1, Parameters.parentSelectionSize, 10, TimeUnit.SECONDS, 
                                                          new ArrayBlockingQueue<>(Parameters.parentSelectionSize), 
                                                          new ThreadPoolExecutor.CallerRunsPolicy());
        while (true){
            ThreadedTournament tt = new ThreadedTournament(this.individuals, parents);
            executor.execute(tt);
            if (parents.size() >= Parameters.parentSelectionSize){
                executor.shutdown();
                return parents;
            }
        }
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
        ExecutorService executor = new ThreadPoolExecutor(10, 15, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(15), new ThreadPoolExecutor.CallerRunsPolicy());
        while (true){
            Individual p1 = parents.get(Utils.randomInt(Parameters.parentSelectionSize-1));
            Individual p2 = Utils.randomPick(parents, p -> p != p1);
            if (Utils.randomDouble()<Parameters.crossoverProbability){
                ThreadedCrossover offspring = new ThreadedCrossover(p1, p2, generationCount, new_population);     
                executor.execute(offspring);
                synchronized (new_population){
                    if (new_population.size() >= Parameters.populationSize){
                        executor.shutdown();
                        return List.of(new_population.toArray(new Individual[]{}));
                    }
                }
            }
        }
    }

    public List<Individual> survivorSelection(List<Individual> parents, List<Individual> offspring){
        int elitism = Parameters.eliteSize;
        offspring.sort(Comparator.comparingDouble(Individual::getFitness));
        parents.sort(Comparator.comparingDouble(Individual::getFitness));
        offspring.subList(offspring.size() - elitism, offspring.size()).clear();
        offspring.addAll(parents.subList(0, elitism));
        return offspring;
    }

}

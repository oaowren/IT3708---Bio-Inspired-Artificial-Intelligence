import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
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
        this.individuals.sort((a,b) -> {
            if (Fitness.getIndividualRouteFitness(a) > Fitness.getIndividualRouteFitness(b)){
                return 1;
            } else if(Fitness.getIndividualRouteFitness(a) < Fitness.getIndividualRouteFitness(b)){
                return -1;
            }
            return 0;
        });
        return this.individuals.get(index);
    }

    public Individual getIndividualByRankAndDeviation(int index, List<Individual> inds){
        if (inds.size()==0){
            return getIndividualByRank(index);
        }
        List<Individual> copy = new ArrayList<>(inds);
        copy.sort((a,b) -> {
            if (Fitness.getIndividualRouteFitness(a) > Fitness.getIndividualRouteFitness(b)){
                return 1;
            } else if(Fitness.getIndividualRouteFitness(a) < Fitness.getIndividualRouteFitness(b)){
                return -1;
            }
            return 0;
        });
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
        int count = 0;
        List<Individual> parents = Collections.synchronizedList(new ArrayList<>());
        // Create parents list of given parentSelectionSize in parameters
        while (true){
            count++;
                ThreadedTournament tt = new ThreadedTournament("Tournament"+count, this.individuals);
                tt.start();
                try{
                    tt.join();
                } catch (InterruptedException e){
                    tt.interrupt();
                }
                synchronized(parents){
                    parents.add(tt.selected);
                    tt.interrupt();
                    if (parents.size() >= Parameters.parentSelectionSize){
                        return parents; 
                    }
                }
            }
    }

    public List<Individual> getIndividualsWithCorrectDuration(){
        return this.individuals.stream().filter(i->i.getDistanceDeviation() == 0.0).collect(Collectors.toList());
    }

    public void setNewPopulation(List<Individual> population){
        this.individuals = population;
    }

    public List<Individual> crossover(List<Individual> parents, int generationCount){
        List<Individual> new_population = Collections.synchronizedList(new ArrayList<>());
        while (true){
            Individual p1 = parents.get(Utils.randomInt(Parameters.parentSelectionSize-1));
            Individual p2 = Utils.randomPick(parents, p-> p != p1);
            if (Utils.randomDouble()<Parameters.crossoverProbability){
                ThreadedCrossover offspring = new ThreadedCrossover("Crossover" + p1.hashCode() + p2.hashCode(), p1, p2, generationCount);                    
                offspring.start();
                try{
                    offspring.join();
                    if (!Objects.isNull(offspring.offspring)){
                        synchronized(new_population){
                            new_population.add(offspring.offspring.x);
                            new_population.add(offspring.offspring.y);
                            if (new_population.size() >= Parameters.populationSize){
                                return new_population;
                            }
                        }
                    }
                } catch (InterruptedException e){
                    offspring.interrupt();
                }
                offspring.interrupt();
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

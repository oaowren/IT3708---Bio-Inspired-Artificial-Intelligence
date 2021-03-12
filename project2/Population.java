import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import DataClasses.*;

public class Population {
    
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

    public Individual getFittestIndividual(boolean route){
        // route denotes whether to only calculate based on pure route or fitness-measure with number of routes as well
        this.individuals.sort((a,b) -> route ? (Fitness.getIndividualRouteFitness(a) > Fitness.getIndividualFitness(b) ? 1 : -1) : (a.getFitness() > b.getFitness() ? 1 : -1));
        return this.individuals.get(0);
        
    }

    public void generatePopulation() {
        Individual newIndividual = null;
        for (int i = 0; i < Parameters.populationSize; i++) {
            newIndividual = new Individual(depots, this.maxNumOfVehicles);
            newIndividual.createRandomIndividual(customers);
            this.individuals.add(newIndividual);
        }
    }

    public List<Individual> tournamentSelection(){
        Random rand = new Random();
        int popSize = this.individuals.size();
        List<Individual> parents = new ArrayList<>();
        // Create parents list of given parentSelectionSize in parameters
        while (parents.size() < Parameters.parentSelectionSize){
            List<Individual> selectedInds = new ArrayList<>();
            // Create individual-list of size defined by tournamentSize
            while (selectedInds.size() < Parameters.tournamentSize){
                Individual i = this.individuals.get(rand.nextInt(popSize));
                // Make sure that an individual does not compete with itself, can still be selected in multiple different tournaments
                if (!selectedInds.contains(i)){
                    selectedInds.add(i);
                }
            }
            // Sort by fitness
            selectedInds.sort(Comparator.comparingDouble(Individual::getFitness));
            double randselect = rand.nextDouble();
            double p = Parameters.tournamentProb;
            /* Calculate a cumulative probability such that you get: 
            Best individual with probability p*(1-p)^0 = p
            Second best individual with probability p*(1-p)
            Third best individual with probability p*(1-p)^2
            And so on, which means that one of the individuals selected for tournament will be chosen with prob = 1*/
            double cumulativeP = 0.0;
            for (int i=0; i< Parameters.tournamentSize; i++){
                // If no selection has been done yet (improbable), default to the worst individual in tournament
                if (i == Parameters.tournamentSize - 1){
                    parents.add(selectedInds.get(i));
                    break;
                } else {
                    cumulativeP += p*Math.pow((1-p), i);
                    if (randselect < cumulativeP){
                        parents.add(selectedInds.get(i));
                        break;
                    }
                }
            }
        }
        return parents; 
    }

    public void setNewPopulation(List<Individual> population){
        this.individuals = population;
    }

    public List<Individual> crossover(List<Individual> parents, int generationCount){
        List<Individual> new_population = new ArrayList<>();
        Random rand = new Random();
        while (new_population.size() < Parameters.populationSize){
            for (Individual p1: parents){
                for (Individual p2:parents){
                    if (rand.nextDouble()<Parameters.crossoverProbability){
                        Tuple<Individual, Individual> offspring = p1.crossover(p2);
                        if (!Objects.isNull(offspring)){
                            new_population.add(offspring.x);
                            new_population.add(offspring.y);
                        }
                    }
                }
            }
        }

        for (Individual individual : new_population) {
            if (rand.nextDouble() >= Parameters.mutationProbability) {
                /*if (generationCount % 10 == 0) {
                    individual.interDepotMutation();
                } else {*/
                    individual.getDepots().get(rand.nextInt(individual.getDepots().size())).intraDepotMutation();
                //}
                individual.calculateFitness();
            }
        }
        return new_population;
    }

    public List<Individual> survivor_selection(List<Individual> parents, List<Individual> offspring){
        int elitism = Parameters.eliteSize;
        offspring.sort(Comparator.comparingDouble(Individual::getFitness));
        parents.sort(Comparator.comparingDouble(Individual::getFitness));
        offspring.subList(offspring.size() - elitism, offspring.size()).clear();
        offspring.addAll(parents.subList(0, elitism));
        return offspring;
    }

}

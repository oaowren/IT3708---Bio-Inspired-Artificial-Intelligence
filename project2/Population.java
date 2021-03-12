import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import DataClasses.*;

public class Population{
    
	private List<Individual> individuals = new ArrayList<>();
    private int maxNumOfVehicles;
    private List<Double> tournamentProbs = new ArrayList<>();

    public static List<Depot> depots;
    public static HashMap<Integer, Customer> customers;

    public void setDepots(List<Depot> newDepots) {
        depots = newDepots;
    }
    public void setCustomers(HashMap<Integer, Customer> newCustomers) {
        customers = newCustomers;
    }
    
    public Population(int maxNumOfVehicles) {
        this.tournamentProbs = getProbs();
        this.maxNumOfVehicles = maxNumOfVehicles;
    }

    private List<Double> getProbs(){
        /* Calculate a cumulative probability such that you get: 
        Best individual with probability p*(1-p)^0 = p
        Second best individual with probability p*(1-p)
        Third best individual with probability p*(1-p)^2
        And so on, which means that one of the individuals selected for tournament will be chosen with prob = 1*/
        double p = Parameters.tournamentProb;
        double cumulativeP = 0.0;
        List<Double> probs = new ArrayList<>();
        for (int i=0; i< Parameters.tournamentSize; i++){
            cumulativeP += p*Math.pow((1-p), i);
            probs.add(cumulativeP);
        }
        return probs;
    }

    public List<Individual> getIndividuals(){
        return this.individuals;
    }

    public Individual getIndividualByRank(int index){
        return Utils.select(this.individuals, index, true);        
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
        List<Individual> parents = new ArrayList<>();
        // Create parents list of given parentSelectionSize in parameters
        while (parents.size() < Parameters.parentSelectionSize){
            List<Individual> selectedInds = new ArrayList<>();
            // Create individual-list of size defined by tournamentSize
            while (selectedInds.size() < Parameters.tournamentSize){
                Individual i = this.individuals.get(Utils.randomInt(Parameters.populationSize));
                // Make sure that an individual does not compete with itself, can still be selected in multiple different tournaments
                if (!selectedInds.contains(i)){
                    selectedInds.add(i);
                }
            }
            int index = 0;
            double randselect = Utils.randomDouble();
            for (int i=0;i<this.tournamentProbs.size();i++){
                if (randselect < this.tournamentProbs.get(i)){
                    index = i;
                    break;
                }
            }
            if (randselect > this.tournamentProbs.get(this.tournamentProbs.size()-1)){
                index = Parameters.tournamentSize - 1;
            }
            parents.add(Utils.select(selectedInds, index, false));
        }
        return parents; 
    }

    public void setNewPopulation(List<Individual> population){
        this.individuals = population;
    }

    public List<Individual> crossover(List<Individual> parents, int generationCount){
        List<Individual> new_population = new ArrayList<>();
        while (new_population.size() < Parameters.populationSize){
            for (Individual p1: parents){
                for (Individual p2:parents){
                    if (Utils.randomDouble()<Parameters.crossoverProbability){
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
            if (Utils.randomDouble() <= Parameters.mutationProbability) {
                if (generationCount % Parameters.interDepotMutationRate == 0 && generationCount != 0) {
                    individual.interDepotMutation();
                } else {
                    Depot randomDepot = Utils.randomPick(individual.getDepots(), (p->p.getAllCustomersInVehicles().size() > 1));
                    randomDepot.intraDepotMutation();
                }
                individual.calculateFitness();
            }
        }
        return new_population;
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

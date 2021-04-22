package Code;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class GeneticAlgorithm {
    
    private Pixel[][] pixels;
    private List<Individual> population;
    private List<List<Individual>> rankedPopulation; 
    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Parameters.threadPoolSize);

    public GeneticAlgorithm(ImageSegmentationIO imageIO){
        this.pixels = imageIO.getPixels();
    }
    
    public List<Individual> getPopulation(){
        return this.population;
    }

    public void runNSGA(){
        int generationCount = 0;
        createPopulation();
        rankPopulation(this.population);
        while (generationCount < Parameters.generationSpan){
            System.out.println(generationCount);
            List<Individual> parents = parentSelection(this.population, true);
            List<Individual> newPopulation = Collections.synchronizedList(new ArrayList<>());
            for (int i = 0; i < Parameters.populationSize / 2; i++){
                executor.execute(()->{
                    Individual parent1 = parents.get(Utils.randomInt(parents.size()));
                    Individual parent2 = parents.get(Utils.randomInt(parents.size()));
                    Tuple<Individual, Individual> offspring = crossover(parent1, parent2);
                    newPopulation.add(offspring.x);
                    newPopulation.add(offspring.y);
                });
            }
            while (newPopulation.size() != Parameters.populationSize){
                ;
            }
            for (Individual individual : newPopulation) {
                if (Utils.randomDouble() < Parameters.mutationProbability) {
                    individual.mutationMergeSegments();
                }
            }
            this.population.addAll(newPopulation);
            this.rankedPopulation = rankPopulation(this.population);
            newPopulationFromRank();
            generationCount ++;
        }
    }

    public void runGA(){
        int generationCount = 0;
        createPopulation();
        while (generationCount < Parameters.generationSpan){
            System.out.println(generationCount);
            List<Individual> parents = parentSelection(this.population, false);
            List<Individual> newPopulation = Collections.synchronizedList(new ArrayList<>());
            for (int i = 0; i < Parameters.populationSize / 2; i++){
                executor.execute(()->{
                    Individual parent1 = parents.get(Utils.randomInt(parents.size()));
                    Individual parent2 = parents.get(Utils.randomInt(parents.size()));
                    Tuple<Individual, Individual> offspring = crossover(parent1, parent2);
                    newPopulation.add(offspring.x);
                    newPopulation.add(offspring.y);
                });
            }
            while (newPopulation.size() != Parameters.populationSize){
                ;
            }
            for (Individual individual : newPopulation) {
                if (Utils.randomDouble() < Parameters.mutationProbability) {
                    individual.mutationMergeSegments();
                }
            }
            this.population = newPopulation;
            generationCount ++;
        }
    }

    public void createPopulation(){
        System.out.println("Creating initial population...");
        List<Individual> newPopulation = Collections.synchronizedList(new ArrayList<>());
        ThreadPoolExecutor tempExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Parameters.threadPoolSize);

        for (int i=0; i< Parameters.populationSize / 2; i++){
            tempExecutor.execute(()->{
                Individual ind = new Individual(this.pixels, Utils.randomInt(5,35));
                System.out.println("Individual created.");
                newPopulation.add(ind);
            });
        }
        tempExecutor.shutdown();
        while (!tempExecutor.isTerminated()){
            ;
        }
        System.out.println("Done creating initial population!");
        this.population = newPopulation;
    }

    public List<Individual> parentSelection(List<Individual> population, boolean useRank){
        List<Individual> selected = new ArrayList<>();
        while (selected.size() < Parameters.parentSelectionSize){
            Individual parent1 = population.get(Utils.randomInt(population.size()));
            Individual parent2 = population.get(Utils.randomInt(population.size()));
            if (Utils.randomDouble() < Parameters.tournamentProb){
                if (useRank ? (parent1.getRank() < parent2.getRank()) : (parent1.getWeightedFitness() < parent2.getWeightedFitness())){
                    selected.add(parent1);
                } else if (useRank ? (parent1.getRank() > parent2.getRank()) : (parent1.getWeightedFitness() > parent2.getWeightedFitness())){
                    selected.add(parent2);
                } else if(useRank && parent1.crowdingDistance > parent2.crowdingDistance){
                    selected.add(parent1);
                } else if(useRank && parent1.crowdingDistance < parent2.crowdingDistance){
                    selected.add(parent2);
                } else {
                    selected.add(Utils.pickRandom(parent1, parent2));
                }
            } else {
                selected.add(Utils.pickRandom(parent1, parent2));
            }
        }
        return selected;
    }

    public Tuple<Individual, Individual> crossover(Individual parent1, Individual parent2){
        
        List<Gene> gene1 = parent1.getGenotype();
        List<Gene> gene2 = parent2.getGenotype();

        if (Utils.randomDouble() < Parameters.crossoverProbability){
            int length = gene1.size();
            int cutpoint = Utils.randomInt(length);
            List<Gene> temp = new ArrayList<>(gene1.subList(cutpoint, length));
            gene1.subList(cutpoint, length).clear();
            gene1.addAll(gene2.subList(cutpoint, length));
            gene2.subList(cutpoint,length).clear();
            gene2.addAll(temp);

        }
        gene1 = mutateRandomGene(gene1);
        gene2 = mutateRandomGene(gene2);
        return new Tuple<>(new Individual(gene1, this.pixels), new Individual(gene2, this.pixels));
    }

    public List<Gene> mutateRandomGene(List<Gene> parentGenotype) {
        if (Utils.randomDouble() < Parameters.mutationProbability) {
            int randomGeneIndex = Utils.randomInt(parentGenotype.size());
            Tuple<Integer, Integer> pixelInd = Utils.genotypeToPixel(randomGeneIndex, this.pixels[0].length);
            List<Gene> legalGenes = this.pixels[pixelInd.y][pixelInd.x].getValidGenes();
            parentGenotype.set(randomGeneIndex, legalGenes.get(Utils.randomInt(legalGenes.size())));
        }
        return parentGenotype;
    }

    public List<List<Individual>> rankPopulation(List<Individual> population){
        List<List<Individual>> rankedPopulation = new ArrayList<>();
        int currentRank = 1;
        while (population.size() > 0){
            List<Individual> dominatingSet = findDominatingSet(population);
            for (Individual i: dominatingSet){
                i.setRank(currentRank);
            }
            rankedPopulation.add(dominatingSet);
            population.removeAll(dominatingSet);
            currentRank++;
        }
        for (List<Individual> front : rankedPopulation){
            population.addAll(front);
        }
        return rankedPopulation;
    }

    private List<Individual> findDominatingSet(List<Individual> population) {
        List<Individual> nonDominatedList = new ArrayList<>();
        // Begin with first member of population
        nonDominatedList.add(population.get(0));
        Set<Individual> dominatedIndividualsSet = new HashSet<>();

        for (Individual individual : population) {
            if (dominatedIndividualsSet.contains(individual)) {
                continue;
            }
            // Add to nonDominated before comparison
            nonDominatedList.add(individual);
            // Compare individual to other individuals currently not dominated
            for (Individual nonDominatedInd : nonDominatedList) {
                if (dominatedIndividualsSet.contains(individual) || nonDominatedInd == individual) {
                    continue;
                }
                // If individual dominates a member of nonDominated, then remove it
                if (individual.dominates(nonDominatedInd)) {
                    dominatedIndividualsSet.add(nonDominatedInd);
                // If individual is dominated by any member in nonDominated, it should not be included
                } else if (nonDominatedInd.dominates(individual)) {
                    dominatedIndividualsSet.add(individual);
                    // No need to compare with the rest of the list as domination has a transitive property
                    break;
                }
            }
        }
        nonDominatedList.removeAll(isDominated);
        return nonDominatedList;
    }

    private void newPopulationFromRank(){
        this.population.clear();
        for (List<Individual> paretoFront: this.rankedPopulation){
            assignCrowdingDistance(paretoFront);
            if (paretoFront.size() <= Parameters.populationSize - this.population.size()){
                this.population.addAll(paretoFront);
            } else {
                List<Individual> copy = new ArrayList<>(paretoFront);
                copy.sort((a,b) -> Double.compare(b.crowdingDistance, a.crowdingDistance));
                this.population.addAll(copy.subList(0, Parameters.populationSize - this.population.size()));
            }
        }
    }

    private void assignCrowdingDistance(List<Individual> paretoFront){
        for (Individual i: paretoFront){
            i.setCrowding(0);
        }
        for (SegmentationCriteria segCrit: SegmentationCriteria.values()) {
            assignCrowdingDistanceToIndividuals(paretoFront, segCrit);
        }
    }

    private void assignCrowdingDistanceToIndividuals(List<Individual> paretoFront, SegmentationCriteria segCrit) {
        paretoFront.sort(SegmentationCriteria.getIndividualComparator(segCrit));

        Individual maxIndividual = paretoFront.get(0);
        Individual minIndividual = paretoFront.get(paretoFront.size()-1);

        maxIndividual.setCrowding(Integer.MAX_VALUE);
        minIndividual.setCrowding(Integer.MAX_VALUE);

        double maxMinSegmentationCriteriaDiff = maxIndividual.getSegmentationCriteriaValue(segCrit) - minIndividual.getSegmentationCriteriaValue(segCrit);
        double segmentationCriteriaDiff;

        for (int i=1; i<paretoFront.size()-1;i++) {
            segmentationCriteriaDiff = paretoFront.get(i+1).getSegmentationCriteriaValue(segCrit) - paretoFront.get(i-1).getSegmentationCriteriaValue(segCrit);
            paretoFront.get(i).setCrowding(
                paretoFront.get(i).crowdingDistance + segmentationCriteriaDiff / maxMinSegmentationCriteriaDiff
            );
        }
    }
}

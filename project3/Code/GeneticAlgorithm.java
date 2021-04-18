package Code;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

public class GeneticAlgorithm {
    
    private Pixel[][] pixels;
    private List<Individual> population; 
    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Parameters.threadPoolSize);
    private ImageSegmentationIO imageIO;

    public GeneticAlgorithm(ImageSegmentationIO imageIO){
        this.pixels = imageIO.getPixels();
        this.imageIO = imageIO;
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
                    //executor.execute(() -> {
                        individual.mutationMergeSegments();
                    //});
                }
            }
            rankPopulation(newPopulation);
            this.population = newPopulation;
            generationCount ++;

            if (Parameters.printEveryNthGeneration > 0 && generationCount % Parameters.printEveryNthGeneration == 0) {
                Individual sample = 
                        population.stream()
                                .sorted((i1, i2) -> Double.compare(i1.getWeightedFitness(), i1.getWeightedFitness()))
                                .collect(Collectors.toList()).get(0);
                executor.execute(()->{
                    imageIO.save("debug", sample, "b", true);
                });
                
            }
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

        for (int i=0; i< Parameters.populationSize; i++){
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
                } else {
                    selected.add(Utils.randomDouble() < 0.5 ? parent1 : parent2);
                }
            } else {
                selected.add(Utils.randomDouble() < 0.5 ? parent1 : parent2);
            }
        }
        return selected;
    }

    public Tuple<Individual, Individual> crossover(Individual parent1, Individual parent2){
        
        List<Gene> gene1 = parent1.getGenotype();
        List<Gene> gene2 = parent2.getGenotype();

        // gene1 = mutateRandomGene(gene1);
        // gene2 = mutateRandomGene(gene2);

        if (Utils.randomDouble() < Parameters.crossoverProbability){
            int length = gene1.size();
            int cutpoint = Utils.randomInt(length);
            List<Gene> temp = new ArrayList<>(gene1.subList(cutpoint, length));
            gene1.subList(cutpoint, length).clear();
            gene1.addAll(gene2.subList(cutpoint, length));
            gene2.subList(cutpoint,length).clear();
            gene2.addAll(temp);


            // for (int i=0; i<length; i++){
            //     if (Utils.randomDouble() < Parameters.singleGeneCrossoverProb){
            //         Gene tmp = gene1.get(i);
            //         gene1.set(i, gene2.get(i));
            //         gene2.set(i, tmp);
            //     }
            // }
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

    public List<Gene> mutateRandomGenes(List<Gene> genotype){
        for (int i = 0; i<genotype.size();i++){
            if (Utils.randomDouble() < Parameters.mutationProbability){
                Tuple<Integer, Integer> pixelInd = Utils.genotypeToPixel(i, this.pixels[0].length);
                List<Gene> legalGenes = this.pixels[pixelInd.y][pixelInd.x].getValidGenes();
                genotype.set(i, legalGenes.get(Utils.randomInt(legalGenes.size())));
            }
        }
        return genotype;
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
        List<Individual> nonDominated = new ArrayList<>();
        // Begin with first member of population
        nonDominated.add(population.get(0));
        Set<Individual> isDominated = new HashSet<>();

        for (Individual individual : population) {
            if (isDominated.contains(individual)) {
                continue;
            }
            // Add to nonDominated before comparison
            nonDominated.add(individual);
            // Compare individual to other individuals currently not dominated
            for (Individual nonDominatedInd : nonDominated) {
                if (isDominated.contains(individual)) {
                    continue;
                }
                if (nonDominatedInd == individual) {
                    continue;
                }
                // If individual dominates a member of nonDominated, then remove it
                if (individual.connectivity < nonDominatedInd.connectivity && 
                    individual.deviation < nonDominatedInd.deviation &&
                    individual.edgeValue < nonDominatedInd.edgeValue){
                    isDominated.add(nonDominatedInd);
                // If individual is dominated by any member in nonDominated, it should not be included
                } else if (individual.connectivity > nonDominatedInd.connectivity && 
                           individual.deviation > nonDominatedInd.deviation &&
                           individual.edgeValue > nonDominatedInd.edgeValue){
                    isDominated.add(individual);
                    // No need to compare with the rest of the list as domination has a transitive property
                    break;
                }
            }
        }
        nonDominated.removeAll(isDominated);
        return nonDominated;
    }
}

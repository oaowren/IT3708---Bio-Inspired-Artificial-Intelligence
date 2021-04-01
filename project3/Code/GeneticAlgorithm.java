package Code;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GeneticAlgorithm {
    
    private Pixel[][] pixels;
    private List<Individual> population; 

    public GeneticAlgorithm(Pixel[][] pixels){
        this.pixels = pixels;
    }

    public List<Individual> getPopulation(){
        return this.population;
    }

    public void run(){
        int generationCount = 0;
        this.createPopulation();
        while (generationCount < Parameters.generationSpan){
            // Create new individuals and rank them
            generationCount ++;
        }
    }

    public void createPopulation(){
        List<Individual> newPopulation = Collections.synchronizedList(new ArrayList<>());

        for (int i=0; i< Parameters.populationSize; i++){
            // TODO: threading?
            Individual ind = new Individual(this.pixels);
            newPopulation.add(ind);
        }
        this.population = newPopulation;
    }
}

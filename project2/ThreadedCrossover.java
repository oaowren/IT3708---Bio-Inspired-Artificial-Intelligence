import java.util.List;
import java.util.Objects;

import DataClasses.Tuple;

public class ThreadedCrossover extends Thread{
    
    public Tuple<Individual, Individual> parents, offspring;
    public int generation;
    public List<Individual> population;

    public ThreadedCrossover(String name, Individual i1, Individual i2, int generation, List<Individual> population){
        super(name);
        this.parents = new Tuple<>(i1, i2);
        this.generation = generation;
        this.population = population;
    }

    public void run(){
        this.offspring = parents.x.crossover(parents.y);
        boolean isNull = Objects.isNull(this.offspring);
        if (!isNull){
            double rand = Utils.randomDouble();
            if (generation % Parameters.interDepotMutationRate == 0 && generation != 0) {
                if (rand <= Parameters.mutationProbability) {
                    this.offspring.x.interDepotMutation();
                } else if (rand >= 1-Parameters.mutationProbability){
                    this.offspring.y.interDepotMutation();
                }
            } else { 
                if (rand <= Parameters.mutationProbability) {
                    Depot randomDepot = Utils.randomPick(this.offspring.x.getDepots(), (p->p.getAllCustomersInVehicles().size() >= 1));
                    randomDepot.intraDepotMutation();
                } else if (rand >= 1 - Parameters.mutationProbability){
                    Depot randomDepot = Utils.randomPick(this.offspring.y.getDepots(), (p->p.getAllCustomersInVehicles().size() >= 1));
                    randomDepot.intraDepotMutation();
                }
            }
            this.offspring.x.calculateFitness();
            this.offspring.y.calculateFitness();
            if (Parameters.useCrowding){
                if (Fitness.distanceCrowding(this.offspring.x, this.parents.x) + Fitness.distanceCrowding(this.offspring.y, this.parents.y) < 
                    Fitness.distanceCrowding(this.offspring.x, this.parents.y) + Fitness.distanceCrowding(this.offspring.y, this.parents.x)){
                    Individual i1 = Fitness.crowding(this.offspring.x, this.parents.x);
                    Individual i2 = Fitness.crowding(this.offspring.y, this.parents.y);
                    this.offspring = new Tuple<>(i1,i2);
                } else {
                    Individual i1 = Fitness.crowding(this.offspring.x, this.parents.y);
                    Individual i2 = Fitness.crowding(this.offspring.y, this.parents.x);
                    this.offspring = new Tuple<>(i1,i2);
                }
            }
            synchronized(this.population){
                    this.population.add(this.offspring.x);
                    this.population.add(this.offspring.y);
            }
        }
    }
}

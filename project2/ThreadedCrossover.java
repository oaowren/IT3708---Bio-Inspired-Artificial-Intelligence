import java.util.Objects;

import DataClasses.Tuple;

public class ThreadedCrossover extends Thread{
    
    public Tuple<Individual, Individual> parents, offspring;
    public int generation;

    public ThreadedCrossover(String name, Individual i1, Individual i2, int generation){
        super(name);
        this.parents = new Tuple<>(i1, i2);
        this.generation = generation;
    }

    public void run(){
        this.offspring = parents.x.crossover(parents.y);
        if (!Objects.isNull(this.offspring)){
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
        }
    }
}

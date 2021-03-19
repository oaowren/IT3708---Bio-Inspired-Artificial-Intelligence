import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ThreadedTournament implements Runnable{
    
    public List<Individual> population, parents;
    public Individual selected;

    public ThreadedTournament(List<Individual> population, List<Individual> parents){
        this.population = population;
        this.parents = parents;
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

    public void run(){
        List<Double> tournamentProbs = getProbs();
        List<Individual> selectedInds = new ArrayList<>();
            // Create individual-list of size defined by tournamentSize
            while (selectedInds.size() < Parameters.tournamentSize){
                synchronized(population){
                    if (population.size() == 0){
                        return;
                    }
                    Individual i = this.population.get(Utils.randomInt(population.size()));
                    selectedInds.add(i);
                    population.remove(i);
                }
                
            }
            // Sort by fitness
            selectedInds.sort(Comparator.comparingDouble(Individual::getFitness));
            double randselect = Utils.randomDouble();
            for (int i=0;i<tournamentProbs.size();i++){
                if (randselect < tournamentProbs.get(i)){
                    this.selected = selectedInds.get(i);
                    break;
                }
            }
            if (randselect > tournamentProbs.get(tournamentProbs.size()-1)){
                this.selected = selectedInds.get(selectedInds.size()-1);
            }
            synchronized(parents){
                if (parents.size() >= Parameters.parentSelectionSize){
                    return;
                }
                parents.add(this.selected);
            }
    }
}

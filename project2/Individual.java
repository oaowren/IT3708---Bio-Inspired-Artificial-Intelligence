import java.util.List;

public class Individual {
    private List<Depot> depots;

    public Individual(List<Depot> depots) {
        this.depots = depots;
    }
    
    public List<Depot> getDepots() {
        return this.depots;
    }

}

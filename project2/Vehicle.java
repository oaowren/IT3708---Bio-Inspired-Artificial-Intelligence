import org.graalvm.compiler.debug.CSVUtil.Escape;

public class Vehicle {
    
    public final int id, maxLoad;
    private String customerSequence;

    public Vehicle(int id, int maxLoad){
        this.id = id;
        this.maxLoad = maxLoad;
        this.customerSequence = "";
    }

    public void visitCustomer(int customerId){
        this.customerSequence += (this.customerSequence.matches("")?"":" ") + Integer.toString(customerId);
    }

    public String getCustomerSequence(){
        return this.customerSequence;
    }
}

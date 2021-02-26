package DataClasses;

import java.util.Objects;
import java.io.Serializable;

public class Customer implements Serializable {
    private static final long serialVersionUID = 2668613946000040734L;
    public final int id;
    public final int x;
    public final int y;
    public final int demand;
    
    public Customer(int id, int x, int y, int demand) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.demand = demand;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Customer)) {
            return false;
        }
        Customer customer = (Customer) o;
        return id == customer.id && x == customer.x && y == customer.y && demand == customer.demand;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, x, y, demand);
    }
    
}

public final class Customer {
    public final int id, x, y, serviceDuration, demand;

    public Customer(int id, int x, int y, int serviceDuration, int demand){
        this.id = id;
        this.x = x;
        this.y = y;
        this.serviceDuration = serviceDuration;
        this.demand = demand;
    }

    @Override
    public String toString() {
        return String.format(
            """
            Customer ID: %d
            Depot x: %d
            Depot y: %d
            """, id, x, y);
    }

}

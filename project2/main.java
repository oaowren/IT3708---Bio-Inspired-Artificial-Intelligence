class Main{
    public static void main(String[] args){
        Depot d = new Depot(1, 80, 4);
        Vehicle v = new Vehicle(1, 80);
        d.addVehicle(v);
        Vehicle v1 = d.getVehicleById(2);
        System.out.println(v1);
        Customer c = new Customer(1, 40, 40, 0, 17);
        Customer c2 = new Customer(2, 50, 50, 0, 17);
        v1.visitCustomer(c.id);
        v1.visitCustomer(c2.id);
        System.out.println(v1.getCustomerSequence());
    }
}
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Comparator;

import DataClasses.Customer;
import DataClasses.Tuple;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Vehicle implements Serializable{
    
    private static final long serialVersionUID = -7246255012616061278L;
    public final int id, maxLoad;
    private int load = 0;
    private List<Customer> customers = new ArrayList<>();
    private Depot depot;

    public Vehicle(int id, int maxLoad) {
        this.id = id;
        this.maxLoad = maxLoad;
    }

    public void visitCustomer(Customer customer){
        if (this.load + customer.demand > this.maxLoad) {
            throw new IllegalStateException("Too much load for current route");
        }
        this.load += customer.demand;
        this.customers.add(customer);
    }

    public void insertCustomer(Customer customer, int index){
        if (this.load + customer.demand > this.maxLoad) {
            throw new IllegalStateException("Too much load for current route");
        }
        this.load += customer.demand;
        this.customers.add(index, customer);
    }

    public void addCustomersToRoute(List<Customer> customers, int index){
        int totalDemand = customers.stream().map(c-> c.demand).reduce(0, (total, demand) -> total + demand);
        if (this.load + totalDemand > this.maxLoad) {
            throw new IllegalStateException("Too much load for current route");
        }
        this.load += totalDemand;
        this.customers.addAll(index, customers);
    }

    public boolean removeCustomerById(int id){
        try{
            Customer customer = this.customers.stream().filter(c-> c.id == id).collect(Collectors.toList()).get(0);
            this.load -= customer.demand;
            this.customers.remove(customer);
            return true;
        } catch (IndexOutOfBoundsException e){
            return false;
        }
    }

    public List<Customer> removeCustomersFromRoute(int startIndex, int endIndex){
        List<Customer> removedCustomers = new ArrayList<>();
        removedCustomers.addAll(this.customers.subList(startIndex, endIndex));
        this.customers.removeAll(removedCustomers);
        int totalDemand = removedCustomers.stream().map(c->c.demand).reduce(0, (total, demand) -> total+demand);
        this.load -= totalDemand;
        return removedCustomers;
    }

    public List<Tuple<Integer, Double>> feasibleInsertions(Customer customer, Fitness f){
        int lengthOfRoute = this.customers.size();
        List<Tuple<Integer, Double>> indexAndFitness = new ArrayList<>();
        for (int i=0; i< lengthOfRoute + 1;i++){
            Vehicle copy = (Vehicle) deepCopy(this);
            try{
                copy.insertCustomer(customer, i);
                indexAndFitness.add(new Tuple<>(i, f.getVehicleFitness(copy, copy.depot)));
            } catch (Exception e){
                ;
            }
        }
        indexAndFitness.sort((a,b) -> a.y > b.y ? 1 : -1);
        return indexAndFitness;
    }

    public void setDepot(Depot depot){
        this.depot = depot;
    }

    public Depot getDepot(){
        return this.depot;
    }

    public String getCustomerSequence(){
        return this.customers.stream()
                            .map(c -> Integer.toString(c.id))
                            .reduce("", (output, c) -> output + (output.matches("")? "":" ") + c);
    }

    public List<Integer> getCustomersId(){
        return this.customers.stream().map(c->c.id).collect(Collectors.toList());
    }

    public List<Customer> getCustomers(){
        return this.customers;
    }

    public boolean isActive() {
        return this.customers.size() > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Vehicle)) {
            return false;
        }
        Vehicle vehicle = (Vehicle) o;
        return id == vehicle.id && maxLoad == vehicle.maxLoad && load == vehicle.load && Objects.equals(customers, vehicle.customers);
    }

    private static Object deepCopy(Object object) {
        try {
          ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
          ObjectOutputStream outputStrm = new ObjectOutputStream(outputStream);
          outputStrm.writeObject(object);
          ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
          ObjectInputStream objInputStream = new ObjectInputStream(inputStream);
          return objInputStream.readObject();
        }
        catch (Exception e) {
          e.printStackTrace();
          return null;
        }
      }

}

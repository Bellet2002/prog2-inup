package se.su.inlupp;

public class Edge<T> {
    private String name;
    private T to;
    private int weight;

    public Edge(String name, T to, int weight) {
        if (name == null || name.isEmpty()) {
            throw new illegalArgumentExeception("Name cannot be null or empty"); 
        
        this.name = name;
        this.to = to;
        if (weight > 0) { 
            this.weight = weight;
        } else {
            throw new IllegalArgumentException("Weight can not be less than zero");
        }
    }

    public T getDestination() {
        return this.to;
    }

    public int getWeight() {
        return this.weight;
    }

    public void setWeight(int weight) {
        if (weight >= 0) {
            this.weight = weight;
        }
        else {
            throw new IllegalArgumentException("Weight can not be less than zero");
        }
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        return "till " + to.toString() + " med " + this.name + " tar " + weight;
    }    
}

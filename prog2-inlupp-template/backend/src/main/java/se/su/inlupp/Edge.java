package se.su.inlupp;

public class Edge<T> {
    private String name;
    private T from;
    private T to;
    private int weight;

    public Edge(String name, T from, T to, int weight) {
        this.name = name;
        this.from = from;
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
        return "Connection name: " + this.name + ", from: " + from.toString() + ", to: " + to.toString() + ", weight: " + weight;
    }    
}

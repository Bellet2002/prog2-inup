package se.su.inlupp;

public class Node {
    private String name;
    private double yCoordinate;
    private double xCoordinate;

    public Node(String name, double x, double y) {
        this.name = name;
        this.yCoordinate = y;
        this.xCoordinate = x;
    }

    public String getName() {
        return name;
    }

    public double getyCoordinate() {
        return yCoordinate;
    }

    public double getxCoordinate() {
        return xCoordinate;
    }

    public String toString() {
        return name;
    }
}

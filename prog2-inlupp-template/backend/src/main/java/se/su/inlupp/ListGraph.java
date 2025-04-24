package se.su.inlupp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class ListGraph<T> implements Graph<T> {
  private Map<T, List<Edge<T>>> graf = new HashMap<>();

  @Override
  public void add(T node) {
    graf.putIfAbsent(node, new ArrayList<>());
  }

  @Override
  public void connect(T node1, T node2, String name, int weight) {
    if (graf.containsKey(node1) && graf.containsKey(node2)) {
      if (weight >= 0) {
        for (Edge edges : graf.get(node1)) {
          if (edges.getDestination() == node2) {
            throw new IllegalStateException("There can only be one edge between nodes");
          }
        }
        graf.get(node1).add(new Edge<T>(name, node1, node2, weight));
        graf.get(node2).add(new Edge<T>(name, node2, node1, weight));
      } else {
        throw new IllegalArgumentException("Weight can not be less than 0");
      }
    } else {
      throw new NoSuchElementException("At least one of the nodes does not exist");
    }
  }

  @Override
  public void setConnectionWeight(T node1, T node2, int weight) {
    throw new UnsupportedOperationException("Unimplemented method 'setConnectionWeight'");
  }

  @Override
  public Set<T> getNodes() {
    Set<T> nodeCopy = new HashSet<>(graf.keySet());
    return nodeCopy;
  }

  @Override
  public Collection<Edge<T>> getEdgesFrom(T node) {
    throw new UnsupportedOperationException("Unimplemented method 'getEdgesFrom'");
  }

  @Override
  public Edge<T> getEdgeBetween(T node1, T node2) {
    if (graf.containsKey(node1) && graf.containsKey(node2)) {
      for (Edge edge : graf.get(node1)) {
        if (edge.getDestination().equals(node2)) {
          return edge;
        }
      }
    } else {
      throw new NoSuchElementException("At least one of the nodes does not exist");
    }
    return null;
  }

  @Override
  public void disconnect(T node1, T node2) {
    if (graf.containsKey(node1) && graf.containsKey(node2)) {
      Edge<T> edge1 = this.getEdgeBetween(node1, node2);
      Edge<T> edge2 = this.getEdgeBetween(node2, node1);
      if (edge1 == null) {
        throw new IllegalStateException("There is no connection between these nodes");
      }
      graf.get(node1).remove(edge1);
      graf.get(node2).remove(edge2);
    } else {
      throw new NoSuchElementException("At least one of the nodes does not exist");
    }
  }

  @Override
  public void remove(T node) {
    if (graf.containsKey(node)) {
      List<Edge<T>> edges = new ArrayList<>(graf.get(node)); //Java gillar tydligen inte när man modifierar listan samtidigt som man itererar över den så jag skapar en kopia som jag itererar samtidigt som jag ändrar orginalet
      for (Edge<T> edge : edges) {
        this.disconnect(node, edge.getDestination());
      }
      graf.remove(node);
    } else {
      throw new NoSuchElementException("The node does not exist");
    }
  }

  @Override
  public boolean pathExists(T from, T to) {
    throw new UnsupportedOperationException("Unimplemented method 'pathExists'");
  }

  @Override
  public List<Edge<T>> getPath(T from, T to) {
    throw new UnsupportedOperationException("Unimplemented method 'getPath'");
  }
}

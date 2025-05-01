package se.su.inlupp;

import java.util.*;

public class ListGraph<T> implements Graph<T> {
  private Map<T, List<Edge<T>>> graf = new HashMap<>();

  @Override
  public void add(T node) {
    graf.putIfAbsent(node, new ArrayList<>());
  }

  @Override
  public void connect(T node1, T node2, String name, int weight) {
    if (nodesExist(node1, node2)){
      for (Edge<T> edges : graf.get(node1)) {
        if (edges.getDestination() == node2) {
          throw new IllegalStateException("There can only be one edge between nodes");
        }
      }
    
   graf.get(node1).add(new Edge<T>(name, node2, weight));
   graf.get(node2).add(new Edge<T>(name, node1, weight));
    }

  @Override
  public void setConnectionWeight(T node1, T node2, int weight) {
    if (nodesExist(node1, node2)) {
      Edge<T> edge1 = getEdgeBetween(node1, node2);
      if (edge1 != null) {
        edge1.setWeight(weight);
        getEdgeBetween(node2, node1).setWeight(weight);
      } else {
        throw new NoSuchElementException("No edge between these nodes");
      }
    }
  }

  @Override
  public Set<T> getNodes() {
    Set<T> nodeCopy = new HashSet<>(graf.keySet());
    return nodeCopy;
  }

  @Override
  public Collection<Edge<T>> getEdgesFrom(T node) {
    if (nodesExist(node)) {
      Collection<Edge<T>> result = graf.get(node);
      return result;
    } else {
      return null;
    }
  }

  @Override
  public Edge<T> getEdgeBetween(T node1, T node2) {
    if (nodesExist(node1, node2)) {
      for (Edge<T> edge : graf.get(node1)) {
        if (edge.getDestination().equals(node2)) {
          return edge;
        }
      }
    }
    return null;
  }

  @Override
  public void disconnect(T node1, T node2) {
    if (nodesExist(node1, node2)) {
      Edge<T> edge1 = this.getEdgeBetween(node1, node2);
      Edge<T> edge2 = this.getEdgeBetween(node2, node1);
      if (edge1 == null) {
        throw new IllegalStateException("There is no connection between these nodes");
      }
      graf.get(node1).remove(edge1);
      graf.get(node2).remove(edge2);
    }
  }

  @Override
  public void remove(T node) {
    if (nodesExist(node)) {
      List<Edge<T>> edges = new ArrayList<>(graf.get(node)); //Java gillar tydligen inte när man modifierar listan samtidigt som man itererar över den så jag skapar en kopia som jag itererar samtidigt som jag ändrar orginalet
      for (Edge<T> edge : edges) {
        this.disconnect(node, edge.getDestination());
      }
      graf.remove(node);
    }
  }

  @Override
  public boolean pathExists(T from, T to) {
    if (graf.containsKey(to) && graf.containsKey(from)) {
      Set<T> seen = new HashSet<>();
      return heightFirst(from, to, seen);
    } else {
      return false;
    }
  }

  @Override
  public List<Edge<T>> getPath(T from, T to) {
    if (nodesExist(from, to) || graf.get(from).isEmpty() || graf.get(to).isEmpty()) {
    Map<T, T> connected = new HashMap<>();
    connected.put(from, null);

    LinkedList<T> queue = new LinkedList<>();
    queue.add(from);

    while (!queue.isEmpty()) {
      T current = queue.pollFirst();
      for (Edge<T> edge : graf.get(current)) {
        T next = edge.getDestination();
        if (!connected.containsKey(next)) {
          connected.put(next, current);
          queue.add(next);
        }
      }
    }
    if (!connected.containsKey(to)) {
      return null;
    }

    LinkedList<Edge<T>> path = new LinkedList<>();
    T current = to;

    while (current != null && !current.equals(from)) {
      T next = connected.get(current);
      Edge<T> edge = getEdgeBetween(next, current);
      current = next;
      path.addFirst(edge);
    }
    return path;
  } else {
    return null;
  }
  }

  public String toString() {
    StringBuilder result = new StringBuilder();
    for (T node : graf.keySet()) {
      result.append("\n" + node.toString());
      for (Edge<T> edge : getEdgesFrom(node)) {
        result.append("\n\t" + edge.toString());
      }
    }
    return result.toString();
  }

  private boolean heightFirst(T from, T to, Set<T> seen) {
    seen.add(from);
    if (from.equals(to)) {
      return true;
    }
    for (Edge<T> edge : graf.get(from)) {
      T temp = edge.getDestination();
      if (!seen.contains(temp)) {
        if (heightFirst(temp, to, seen)) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean nodesExist(T node1, T node2) {
    if (graf.containsKey(node1)) {
      if (graf.containsKey(node2)) {
        return true;
      } else {
        throw new NoSuchElementException("Node: " + node2.toString() + " does not exist");
      }
    } else {
      throw new NoSuchElementException("Node: " + node1.toString() + " does not exist");
    }
  }

  private boolean nodesExist(T node) {
    if (graf.containsKey(node)) {
      return true;
    } else {
      throw new NoSuchElementException("The node does not exist");
    }
  }
}

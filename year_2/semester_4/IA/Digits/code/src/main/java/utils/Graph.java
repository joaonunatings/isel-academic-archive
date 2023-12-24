package utils;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class Graph {

    private LinkedList<Edge> edges = new LinkedList<>();

    private int numberOfNodes;

    public Graph(int numberOfNodes) {
        this.numberOfNodes = numberOfNodes;
    }

    public void add(Node leftNode, Node rightNode, double weight) {
        edges.add(new Edge(leftNode, rightNode, weight));
    }

    public List<Edge> getEdgesContaining(Node node) {
        return edges.stream().filter(e -> e.getLeftNode() == node || e.getRightNode() == node).collect(LinkedList::new, LinkedList::add, LinkedList::addAll);
    }

    public List<Edge> getEdgesContainingLeft(Node node) {
        return edges.stream().filter(e -> e.getLeftNode() == node).collect(LinkedList::new, LinkedList::add, LinkedList::addAll);
    }

    public List<Edge> getEdgesContainingRight(Node node) {
        return edges.stream().filter(e -> e.getRightNode() == node).collect(LinkedList::new, LinkedList::add, LinkedList::addAll);
    }
}

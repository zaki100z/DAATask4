package graph.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.*;

public class Graph {
    @JsonProperty("directed")
    private boolean directed;

    @JsonProperty("n")
    private int vertexCount;

    @JsonProperty("edges")
    private List<Edge> edges;

    @JsonProperty("source")
    private int source;

    @JsonProperty("weight_model")
    private String weightModel;

    // Default constructor for Jackson
    public Graph() {
        this.edges = new ArrayList<>(); // Initialize empty list to prevent null
    }

    public Graph(int vertexCount, List<Edge> edges, boolean directed) {
        this.vertexCount = vertexCount;
        this.edges = edges != null ? edges : new ArrayList<>();
        this.directed = directed;
    }

    // Utility methods that don't interfere with Jackson
    public List<List<Integer>> getAdjacencyList() {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < vertexCount; i++) {
            adj.add(new ArrayList<>());
        }

        for (Edge edge : edges) {
            adj.get(edge.getU()).add(edge.getV());
            if (!directed) {
                adj.get(edge.getV()).add(edge.getU());
            }
        }
        return adj;
    }

    public Map<Integer, List<Edge>> getWeightedAdjacencyList() {
        Map<Integer, List<Edge>> adj = new HashMap<>();
        for (int i = 0; i < vertexCount; i++) {
            adj.put(i, new ArrayList<>());
        }

        for (Edge edge : edges) {
            adj.get(edge.getU()).add(edge);
            if (!directed) {
                Edge reverseEdge = new Edge(edge.getV(), edge.getU(), edge.getW());
                adj.get(edge.getV()).add(reverseEdge);
            }
        }
        return adj;
    }

    // Getters and setters
    public boolean isDirected() { return directed; }
    public int getVertexCount() { return vertexCount; }
    public List<Edge> getEdges() {
        if (edges == null) {
            edges = new ArrayList<>();
        }
        return edges;
    }
    public int getSource() { return source; }
    public String getWeightModel() { return weightModel; }

    public void setDirected(boolean directed) { this.directed = directed; }
    public void setVertexCount(int vertexCount) { this.vertexCount = vertexCount; }
    public void setEdges(List<Edge> edges) {
        this.edges = edges != null ? edges : new ArrayList<>();
    }
    public void setSource(int source) { this.source = source; }
    public void setWeightModel(String weightModel) { this.weightModel = weightModel; }
}
package graph.dagsp;

import graph.metrics.Metrics;

public class ShortestPathResult {
    private final int[] distances;
    private final int[] predecessors;
    private final Metrics metrics;

    public ShortestPathResult(int[] distances, int[] predecessors, Metrics metrics) {
        this.distances = distances;
        this.predecessors = predecessors;
        this.metrics = metrics;
    }

    // Getters
    public int[] getDistances() { return distances; }
    public int[] getPredecessors() { return predecessors; }
    public Metrics getMetrics() { return metrics; }
}
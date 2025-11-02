package graph.scc;

import graph.model.Graph;
import graph.metrics.Metrics;
import java.util.List;

public class SCCResult {
    private final List<List<Integer>> sccs;
    private final Graph condensationGraph;
    private final Metrics metrics;

    public SCCResult(List<List<Integer>> sccs, Graph condensationGraph, Metrics metrics) {
        this.sccs = sccs;
        this.condensationGraph = condensationGraph;
        this.metrics = metrics;
    }

    // Getters
    public List<List<Integer>> getSccs() { return sccs; }
    public Graph getCondensationGraph() { return condensationGraph; }
    public Metrics getMetrics() { return metrics; }
}
package graph.topo;

import graph.metrics.Metrics;
import java.util.List;

public class TopoResult {
    private final List<Integer> topologicalOrder;
    private final Metrics metrics;

    public TopoResult(List<Integer> topologicalOrder, Metrics metrics) {
        this.topologicalOrder = topologicalOrder;
        this.metrics = metrics;
    }

    // Getters
    public List<Integer> getTopologicalOrder() { return topologicalOrder; }
    public Metrics getMetrics() { return metrics; }
}
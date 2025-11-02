package graph.dagsp;

import graph.metrics.Metrics;
import java.util.List;

public class CriticalPathResult {
    private final List<Integer> criticalPath;
    private final int maxDistance;
    private final int[] distances;
    private final int[] predecessors;
    private final Metrics metrics;

    public CriticalPathResult(List<Integer> criticalPath, int maxDistance,
                              int[] distances, int[] predecessors, Metrics metrics) {
        this.criticalPath = criticalPath;
        this.maxDistance = maxDistance;
        this.distances = distances;
        this.predecessors = predecessors;
        this.metrics = metrics;
    }

    // Getters
    public List<Integer> getCriticalPath() { return criticalPath; }
    public int getMaxDistance() { return maxDistance; }
    public int[] getDistances() { return distances; }
    public int[] getPredecessors() { return predecessors; }
    public Metrics getMetrics() { return metrics; }
}
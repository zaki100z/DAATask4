package graph.topo;

import graph.model.Graph;
import graph.metrics.Metrics;
import java.util.*;

public class KahnsAlgorithm {
    private Metrics metrics;

    public KahnsAlgorithm() {
        this.metrics = new Metrics();
    }

    public TopoResult topologicalSort(Graph graph) {
        metrics.startTimer();

        int n = graph.getVertexCount();
        List<List<Integer>> adj = graph.getAdjacencyList();
        int[] inDegree = new int[n];

        // Calculate in-degrees
        for (int u = 0; u < n; u++) {
            for (int v : adj.get(u)) {
                metrics.incrementOperation("Edge processing");
                inDegree[v]++;
            }
        }

        // Initialize queue with vertices having 0 in-degree
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                metrics.incrementOperation("Queue operations");
                queue.offer(i);
            }
        }

        List<Integer> topoOrder = new ArrayList<>();
        int visited = 0;

        while (!queue.isEmpty()) {
            int u = queue.poll();
            metrics.incrementOperation("Queue operations");
            topoOrder.add(u);

            for (int v : adj.get(u)) {
                metrics.incrementOperation("Edge processing");
                inDegree[v]--;
                if (inDegree[v] == 0) {
                    queue.offer(v);
                }
            }
            visited++;
        }

        metrics.stopTimer();

        if (visited != n) {
            throw new IllegalArgumentException("Graph has cycles, topological sort not possible");
        }

        return new TopoResult(topoOrder, metrics);
    }
}
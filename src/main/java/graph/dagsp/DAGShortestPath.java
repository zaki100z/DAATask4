package graph.dagsp;

import graph.model.Graph;
import graph.metrics.Metrics;
import java.util.*;

public class DAGShortestPath {
    private Metrics metrics;

    public DAGShortestPath() {
        this.metrics = new Metrics();
    }

    public ShortestPathResult findShortestPaths(Graph graph, List<Integer> topologicalOrder, int source) {
        metrics.startTimer();

        int n = graph.getVertexCount();
        int[] dist = new int[n];
        int[] prev = new int[n];

        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(prev, -1);
        dist[source] = 0;

        Map<Integer, List<graph.model.Edge>> adj = graph.getWeightedAdjacencyList();

        // Process vertices in topological order
        for (int u : topologicalOrder) {
            metrics.incrementOperation("Vertex processing");

            if (dist[u] != Integer.MAX_VALUE) {
                for (graph.model.Edge edge : adj.get(u)) {
                    metrics.incrementOperation("Edge relaxation");
                    int v = edge.getV();
                    int weight = edge.getW();

                    if (dist[u] + weight < dist[v]) {
                        dist[v] = dist[u] + weight;
                        prev[v] = u;
                    }
                }
            }
        }

        metrics.stopTimer();
        return new ShortestPathResult(dist, prev, metrics);
    }

    public CriticalPathResult findCriticalPath(Graph graph, List<Integer> topologicalOrder) {
        metrics.startTimer();

        int n = graph.getVertexCount();
        int[] dist = new int[n];
        int[] prev = new int[n];

        Arrays.fill(dist, Integer.MIN_VALUE);
        Arrays.fill(prev, -1);

        // Find source with 0 in-degree
        int source = findSource(graph);
        dist[source] = 0;

        Map<Integer, List<graph.model.Edge>> adj = graph.getWeightedAdjacencyList();

        // Process vertices in topological order for longest path
        for (int u : topologicalOrder) {
            metrics.incrementOperation("Vertex processing");

            if (dist[u] != Integer.MIN_VALUE) {
                for (graph.model.Edge edge : adj.get(u)) {
                    metrics.incrementOperation("Edge relaxation");
                    int v = edge.getV();
                    int weight = edge.getW();

                    if (dist[u] + weight > dist[v]) {
                        dist[v] = dist[u] + weight;
                        prev[v] = u;
                    }
                }
            }
        }

        // Find the critical path (longest path)
        int maxDist = Integer.MIN_VALUE;
        int endVertex = -1;
        for (int i = 0; i < n; i++) {
            if (dist[i] > maxDist) {
                maxDist = dist[i];
                endVertex = i;
            }
        }

        List<Integer> criticalPath = reconstructPath(prev, endVertex);
        metrics.stopTimer();

        return new CriticalPathResult(criticalPath, maxDist, dist, prev, metrics);
    }

    private int findSource(Graph graph) {
        int[] inDegree = new int[graph.getVertexCount()];
        for (graph.model.Edge edge : graph.getEdges()) {
            inDegree[edge.getV()]++;
        }

        for (int i = 0; i < inDegree.length; i++) {
            if (inDegree[i] == 0) {
                return i;
            }
        }
        return 0; // fallback
    }

    private List<Integer> reconstructPath(int[] prev, int endVertex) {
        List<Integer> path = new ArrayList<>();
        for (int v = endVertex; v != -1; v = prev[v]) {
            path.add(v);
        }
        Collections.reverse(path);
        return path;
    }
}
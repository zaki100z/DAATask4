package graph.scc;

import graph.model.Graph;
import graph.metrics.Metrics;
import java.util.*;

public class TarjanSCC {
    private int index;
    private int[] indices;
    private int[] lowLinks;
    private boolean[] onStack;
    private Stack<Integer> stack;
    private List<List<Integer>> sccs;
    private Metrics metrics;

    public TarjanSCC() {
        this.metrics = new Metrics();
    }

    public SCCResult findSCCs(Graph graph) {
        metrics.startTimer();
        int n = graph.getVertexCount();
        indices = new int[n];
        lowLinks = new int[n];
        onStack = new boolean[n];
        stack = new Stack<>();
        sccs = new ArrayList<>();
        index = 0;

        Arrays.fill(indices, -1);

        List<List<Integer>> adj = graph.getAdjacencyList();

        for (int i = 0; i < n; i++) {
            metrics.incrementOperation("DFS visits");
            if (indices[i] == -1) {
                strongConnect(i, adj);
            }
        }

        metrics.stopTimer();
        return new SCCResult(sccs, buildCondensationGraph(graph, sccs), metrics);
    }

    private void strongConnect(int v, List<List<Integer>> adj) {
        indices[v] = index;
        lowLinks[v] = index;
        index++;
        stack.push(v);
        onStack[v] = true;

        for (int w : adj.get(v)) {
            metrics.incrementOperation("Edge traversals");
            if (indices[w] == -1) {
                strongConnect(w, adj);
                lowLinks[v] = Math.min(lowLinks[v], lowLinks[w]);
            } else if (onStack[w]) {
                lowLinks[v] = Math.min(lowLinks[v], indices[w]);
            }
        }

        if (lowLinks[v] == indices[v]) {
            List<Integer> scc = new ArrayList<>();
            int w;
            do {
                w = stack.pop();
                onStack[w] = false;
                scc.add(w);
            } while (w != v);
            sccs.add(scc);
        }
    }

    private Graph buildCondensationGraph(Graph originalGraph, List<List<Integer>> sccs) {
        // Map each vertex to its SCC index
        int[] sccIndex = new int[originalGraph.getVertexCount()];
        for (int i = 0; i < sccs.size(); i++) {
            for (int vertex : sccs.get(i)) {
                sccIndex[vertex] = i;
            }
        }

        // Create edges between SCCs
        Set<String> edges = new HashSet<>();
        List<graph.model.Edge> condensationEdges = new ArrayList<>();

        for (graph.model.Edge edge : originalGraph.getEdges()) {
            int uScc = sccIndex[edge.getU()];
            int vScc = sccIndex[edge.getV()];

            if (uScc != vScc) {
                String edgeKey = uScc + "->" + vScc;
                if (!edges.contains(edgeKey)) {
                    edges.add(edgeKey);
                    condensationEdges.add(new graph.model.Edge(uScc, vScc, edge.getW()));
                }
            }
        }

        return new Graph(sccs.size(), condensationEdges, true);
    }
}
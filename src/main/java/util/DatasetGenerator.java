package util;

import graph.model.Graph;
import graph.model.Edge;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class DatasetGenerator {
    private static final Random random = new Random(42);

    public static void main(String[] args) throws IOException {
        generateAllDatasets();
    }

    public static void generateAllDatasets() throws IOException {
        // Create data directory
        new File("data").mkdirs();

        System.out.println("=== Generating Test Datasets ===");

        try {
            generateSimpleTestDatasets();
            System.out.println("✓ All datasets generated successfully!");
        } catch (Exception e) {
            System.out.println("⚠ Error during dataset generation: " + e.getMessage());
            throw e;
        }
    }

    private static void generateSimpleTestDatasets() throws IOException {
        // Generate exactly 6 datasets without any random bounds issues

        // Dataset 1: Simple linear DAG
        Graph graph1 = createLinearDAG(8);
        saveGraph(graph1, "small_1");

        // Dataset 2: Simple DAG with branches
        Graph graph2 = createBranchedDAG(10);
        saveGraph(graph2, "small_2");

        // Dataset 3: Simple graph with one cycle
        Graph graph3 = createGraphWithSingleCycle(9);
        saveGraph(graph3, "small_3");

        // Dataset 4: Medium DAG
        Graph graph4 = createMediumDAG(15);
        saveGraph(graph4, "medium_1");

        // Dataset 5: Medium graph with cycles
        Graph graph5 = createMediumGraphWithCycles(18);
        saveGraph(graph5, "medium_2");

        // Dataset 6: Simple large DAG (no complex random logic)
        Graph graph6 = createSimpleLargeDAG(25);
        saveGraph(graph6, "large_1");
    }

    // Simple linear DAG: 0->1->2->3->...
    private static Graph createLinearDAG(int n) {
        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < n - 1; i++) {
            edges.add(new Edge(i, i + 1, random.nextInt(5) + 1));
        }
        return createGraph(n, edges, 0);
    }

    // DAG with branches
    private static Graph createBranchedDAG(int n) {
        List<Edge> edges = new ArrayList<>();

        // Main chain
        for (int i = 0; i < n - 1; i++) {
            edges.add(new Edge(i, i + 1, random.nextInt(5) + 1));
        }

        // Add some fixed branches (no random bounds)
        if (n >= 4) edges.add(new Edge(0, 3, 2));
        if (n >= 6) edges.add(new Edge(2, 5, 3));
        if (n >= 8) edges.add(new Edge(4, 7, 4));

        return createGraph(n, edges, 0);
    }

    // Graph with a single cycle
    private static Graph createGraphWithSingleCycle(int n) {
        List<Edge> edges = new ArrayList<>();

        // Create a small cycle: 0->1->2->0
        if (n >= 3) {
            edges.add(new Edge(0, 1, 2));
            edges.add(new Edge(1, 2, 3));
            edges.add(new Edge(2, 0, 1));
        }

        // Add linear chain for remaining vertices
        for (int i = 3; i < n - 1; i++) {
            edges.add(new Edge(i, i + 1, random.nextInt(5) + 1));
        }

        // Connect cycle to the rest if possible
        if (n > 3) {
            edges.add(new Edge(0, 3, 2));
        }

        return createGraph(n, edges, 0);
    }

    // Medium-sized DAG - completely deterministic
    private static Graph createMediumDAG(int n) {
        List<Edge> edges = new ArrayList<>();

        // Create a deterministic hierarchical structure
        for (int i = 0; i < n; i++) {
            // Each node connects to next 2-3 nodes (if they exist)
            int connections = 2;
            for (int j = 1; j <= connections; j++) {
                int target = i + j;
                if (target < n) {
                    edges.add(new Edge(i, target, (i + j) % 5 + 1));
                }
            }
        }

        return createGraph(n, edges, 0);
    }

    // Medium graph with some cycles - deterministic
    private static Graph createMediumGraphWithCycles(int n) {
        List<Edge> edges = new ArrayList<>();

        // Add a linear base
        for (int i = 0; i < n - 1; i++) {
            edges.add(new Edge(i, i + 1, (i % 5) + 1));
        }

        // Add some fixed cycles (no random bounds)
        if (n >= 6) {
            // Cycle 1: 1->3->5->1
            edges.add(new Edge(1, 3, 2));
            edges.add(new Edge(3, 5, 3));
            edges.add(new Edge(5, 1, 1));
        }

        if (n >= 8) {
            // Cycle 2: 2->4->6->2
            edges.add(new Edge(2, 4, 2));
            edges.add(new Edge(4, 6, 3));
            edges.add(new Edge(6, 2, 1));
        }

        return createGraph(n, edges, 0);
    }

    // Simple large DAG - completely deterministic, no random bounds
    private static Graph createSimpleLargeDAG(int n) {
        List<Edge> edges = new ArrayList<>();

        // Create a deterministic hierarchical structure
        // Layer 1: 0-7
        // Layer 2: 8-16
        // Layer 3: 17-24

        // Connect layer 1 to layer 2
        for (int i = 0; i < 8 && i < n; i++) {
            for (int j = 8; j < 17 && j < n; j++) {
                if ((i + j) % 3 == 0) { // Deterministic condition
                    edges.add(new Edge(i, j, (i + j) % 5 + 1));
                }
            }
        }

        // Connect layer 2 to layer 3
        for (int i = 8; i < 17 && i < n; i++) {
            for (int j = 17; j < n; j++) {
                if ((i + j) % 2 == 0) { // Deterministic condition
                    edges.add(new Edge(i, j, (i + j) % 5 + 1));
                }
            }
        }

        // Ensure basic connectivity with a linear chain
        for (int i = 0; i < n - 1; i++) {
            // Only add if no edge exists between these vertices
            boolean edgeExists = false;
            for (Edge edge : edges) {
                if (edge.getU() == i && edge.getV() == i + 1) {
                    edgeExists = true;
                    break;
                }
            }
            if (!edgeExists) {
                edges.add(new Edge(i, i + 1, 1));
            }
        }

        return createGraph(n, edges, 0);
    }

    private static Graph createGraph(int n, List<Edge> edges, int source) {
        Graph graph = new Graph(n, edges, true);
        graph.setSource(source);
        graph.setWeightModel("edge");
        return graph;
    }

    private static void saveGraph(Graph graph, String filename) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File("data/" + filename + ".json");

        // Ensure edges list is not null and properly initialized
        if (graph.getEdges() == null) {
            graph.setEdges(new ArrayList<>());
        }

        mapper.writerWithDefaultPrettyPrinter().writeValue(file, graph);
        System.out.println("✓ Generated: " + filename + ".json with " +
                graph.getVertexCount() + " vertices and " +
                graph.getEdges().size() + " edges");
    }
}
import graph.scc.*;
import graph.topo.*;
import graph.dagsp.*;
import graph.model.Graph;
import graph.metrics.Metrics;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("=== Smart City Scheduling System ===");

            // Step 1: Generate datasets
            System.out.println("\nStep 1: Generating datasets...");
            boolean datasetsGenerated = generateDatasetsSafely();

            if (datasetsGenerated) {
                System.out.println("Datasets generated successfully");
            } else {
                System.out.println("Using existing datasets or fallback");
            }
            // Step 2: Load graph
            System.out.println("\nStep 2: Loading graph...");
            Graph graph = loadGraphSafely();
            System.out.println("✓ Graph loaded: " + graph.getVertexCount() + " vertices, " +
                    graph.getEdges().size() + " edges");

            // Step 3: Run analysis
            runCompleteAnalysis(graph);

        } catch (Exception e) {
            System.err.println("Fatal error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean generateDatasetsSafely() {
        try {
            util.DatasetGenerator.generateAllDatasets();
            return true;
        } catch (Exception e) {
            System.out.println("Dataset generation completed with some issues: " + e.getMessage());
            // Don't throw, just continue with existing files
            return false;
        }
    }

    private static Graph loadGraphSafely() {
        ObjectMapper mapper = new ObjectMapper();

        // Try to load from generated datasets in order
        String[] filesToTry = {
                "data/small_1.json", "data/small_2.json", "data/small_3.json",
                "data/medium_1.json", "data/medium_2.json", "data/large_1.json"
        };

        for (String filename : filesToTry) {
            try {
                File file = new File(filename);
                if (file.exists() && file.length() > 0) {
                    System.out.println("Trying to load: " + filename);
                    Graph graph = mapper.readValue(file, Graph.class);

                    // Validate the loaded graph
                    if (isValidGraph(graph)) {
                        System.out.println("Successfully loaded: " + filename);
                        return graph;
                    } else {
                        System.out.println("Loaded graph is invalid, trying next...");
                    }
                }
            } catch (Exception e) {
                System.out.println("Failed to load " + filename + ": " + e.getMessage());
            }
        }

        // Fallback: create a simple graph
        System.out.println("Using fallback graph");
        return createFallbackGraph();
    }

    private static boolean isValidGraph(Graph graph) {
        return graph != null &&
                graph.getVertexCount() > 0 &&
                graph.getEdges() != null &&
                !graph.getEdges().isEmpty();
    }

    private static Graph createFallbackGraph() {
        // Create a reliable, simple graph for testing
        List<graph.model.Edge> edges = Arrays.asList(
                new graph.model.Edge(0, 1, 5),
                new graph.model.Edge(0, 2, 3),
                new graph.model.Edge(1, 3, 2),
                new graph.model.Edge(1, 4, 6),
                new graph.model.Edge(2, 3, 4),
                new graph.model.Edge(2, 5, 7),
                new graph.model.Edge(3, 6, 3),
                new graph.model.Edge(4, 6, 2),
                new graph.model.Edge(5, 6, 4)
        );
        Graph graph = new Graph(7, edges, true);
        graph.setSource(0);
        graph.setWeightModel("edge");
        return graph;
    }

    private static void runCompleteAnalysis(Graph graph) {
        try {
            // 1. SCC Detection
            System.out.println("\n" + "=".repeat(50));
            System.out.println("1. STRONGLY CONNECTED COMPONENTS ANALYSIS");
            System.out.println("=".repeat(50));

            TarjanSCC sccFinder = new TarjanSCC();
            SCCResult sccResult = sccFinder.findSCCs(graph);

            List<List<Integer>> sccs = sccResult.getSccs();
            System.out.println("✓ Found " + sccs.size() + " SCCs:");
            for (int i = 0; i < sccs.size(); i++) {
                System.out.println("  Component " + i + ": " + sccs.get(i) + " (size: " + sccs.get(i).size() + ")");
            }

            Graph condensationGraph = sccResult.getCondensationGraph();
            System.out.println("✓ Condensation graph: " + condensationGraph.getVertexCount() +
                    " nodes, " + condensationGraph.getEdges().size() + " edges");

            // 2. Topological Sort
            System.out.println("\n" + "=".repeat(50));
            System.out.println("2.TOPOLOGICAL ORDERING");
            System.out.println("=".repeat(50));

            if (condensationGraph.getVertexCount() > 0) {
                try {
                    KahnsAlgorithm topoSort = new KahnsAlgorithm();
                    TopoResult topoResult = topoSort.topologicalSort(condensationGraph);

                    System.out.println("✓Topological order: " + topoResult.getTopologicalOrder());

                    // 3. Path Analysis
                    System.out.println("\n" + "=".repeat(50));
                    System.out.println("3. PATH ANALYSIS");
                    System.out.println("=".repeat(50));

                    runPathAnalysis(condensationGraph, topoResult, sccs, graph.getSource());

                } catch (IllegalArgumentException e) {
                    System.out.println("Cannot perform topological sort: " + e.getMessage());
                    System.out.println("The condensation graph may contain cycles.");
                }
            } else {
                System.out.println("Condensation graph is empty.");
            }

            // Print performance metrics
            printPerformanceMetrics(sccResult.getMetrics());

            System.out.println("\n" + "=".repeat(50));
            System.out.println("ANALYSIS COMPLETED SUCCESSFULLY!");
            System.out.println("=".repeat(50));

        } catch (Exception e) {
            System.err.println("Error in analysis: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void runPathAnalysis(Graph condensationGraph, TopoResult topoResult,
                                        List<List<Integer>> sccs, int originalSource) {
        DAGShortestPath pathFinder = new DAGShortestPath();

        // Map original source to component
        int sourceComponent = mapVertexToComponent(originalSource, sccs);
        System.out.println("Source vertex " + originalSource + " → Component " + sourceComponent);

        // Shortest paths
        System.out.println("SHORTEST PATHS from component " + sourceComponent + ":");
        ShortestPathResult shortestResult = pathFinder.findShortestPaths(
                condensationGraph, topoResult.getTopologicalOrder(), sourceComponent
        );

        int[] dist = shortestResult.getDistances();
        for (int i = 0; i < dist.length; i++) {
            if (dist[i] != Integer.MAX_VALUE) {
                System.out.println("  To component " + i + ": " + dist[i]);
            } else {
                System.out.println("  To component " + i + ": unreachable");
            }
        }

        // Critical path (longest path)
        System.out.println("CRITICAL PATH ANALYSIS:");
        CriticalPathResult criticalResult = pathFinder.findCriticalPath(
                condensationGraph, topoResult.getTopologicalOrder()
        );

        System.out.println("Critical path: " + criticalResult.getCriticalPath());
        System.out.println("Critical path length: " + criticalResult.getMaxDistance());

        // Print path metrics
        printPathMetrics(shortestResult.getMetrics(), criticalResult.getMetrics());
    }

    private static int mapVertexToComponent(int vertex, List<List<Integer>> sccs) {
        for (int i = 0; i < sccs.size(); i++) {
            if (sccs.get(i).contains(vertex)) {
                return i;
            }
        }
        return 0; // Default to first component
    }

    private static void printPerformanceMetrics(Metrics sccMetrics) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("PERFORMANCE METRICS");
        System.out.println("=".repeat(50));
        System.out.println("SCC Detection:");
        System.out.println("  Time: " + (sccMetrics.getElapsedTime() / 1_000_000.0) + " ms");
        System.out.println("  Operations: " + sccMetrics.getAllOperationCounts());
    }

    private static void printPathMetrics(Metrics shortestMetrics, Metrics criticalMetrics) {
        System.out.println("Shortest Path:");
        System.out.println("  Time: " + (shortestMetrics.getElapsedTime() / 1_000_000.0) + " ms");
        System.out.println("Critical Path:");
        System.out.println("  Time: " + (criticalMetrics.getElapsedTime() / 1_000_000.0) + " ms");
    }
}
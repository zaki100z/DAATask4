package graph;

import graph.scc.TarjanSCC;
import graph.topo.KahnsAlgorithm;
import graph.dagsp.DAGShortestPath;
import graph.model.Graph;
import graph.model.Edge;
import org.junit.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.*;

public class SCCTest {

    @Test
    public void testSCCSimpleCycle() {
        Graph graph = new Graph(4, Arrays.asList(
                new Edge(0, 1, 1),
                new Edge(1, 2, 1),
                new Edge(2, 3, 1),
                new Edge(3, 0, 1)
        ), true);

        TarjanSCC sccFinder = new TarjanSCC();
        var result = sccFinder.findSCCs(graph);

        assertEquals(1, result.getSccs().size());
        assertEquals(4, result.getSccs().get(0).size());
    }

    @Test
    public void testSCCMultipleComponents() {
        Graph graph = new Graph(6, Arrays.asList(
                new Edge(0, 1, 1), new Edge(1, 0, 1), // SCC 1: 0,1
                new Edge(2, 3, 1), new Edge(3, 2, 1), // SCC 2: 2,3
                new Edge(4, 5, 1), new Edge(5, 4, 1)  // SCC 3: 4,5
        ), true);

        TarjanSCC sccFinder = new TarjanSCC();
        var result = sccFinder.findSCCs(graph);

        assertEquals(3, result.getSccs().size());
        assertEquals(3, result.getCondensationGraph().getVertexCount());
    }

    @Test
    public void testSCCSingleVertex() {
        Graph graph = new Graph(1, Arrays.asList(), true);

        TarjanSCC sccFinder = new TarjanSCC();
        var result = sccFinder.findSCCs(graph);

        assertEquals(1, result.getSccs().size());
        assertEquals(1, result.getSccs().get(0).size());
        assertEquals(0, result.getSccs().get(0).get(0).intValue());
    }

    @Test
    public void testSCCNoEdges() {
        Graph graph = new Graph(5, Arrays.asList(), true);

        TarjanSCC sccFinder = new TarjanSCC();
        var result = sccFinder.findSCCs(graph);

        assertEquals(5, result.getSccs().size());
        for (int i = 0; i < 5; i++) {
            assertEquals(1, result.getSccs().get(i).size());
            assertEquals(i, result.getSccs().get(i).get(0).intValue());
        }
    }

    @Test
    public void testSCCComplexGraph() {
        // Graph with multiple cycles and isolated components
        Graph graph = new Graph(8, Arrays.asList(
                new Edge(0, 1, 1), new Edge(1, 0, 1), // Cycle 1: 0,1
                new Edge(2, 3, 1), new Edge(3, 4, 1), new Edge(4, 2, 1), // Cycle 2: 2,3,4
                new Edge(5, 6, 1), // Simple edge
                new Edge(0, 2, 1), // Connection between cycles
                new Edge(3, 5, 1)  // Connection to simple edge
        ), true);

        TarjanSCC sccFinder = new TarjanSCC();
        var result = sccFinder.findSCCs(graph);

        // Should have multiple SCCs including cycles
        assertTrue(result.getSccs().size() >= 3);

        // Check that cycles are detected as single components
        boolean foundCycle1 = false, foundCycle2 = false;
        for (List<Integer> scc : result.getSccs()) {
            if (scc.contains(0) && scc.contains(1)) {
                foundCycle1 = true;
                assertEquals(2, scc.size());
            }
            if (scc.contains(2) && scc.contains(3) && scc.contains(4)) {
                foundCycle2 = true;
                assertEquals(3, scc.size());
            }
        }
        assertTrue(foundCycle1);
        assertTrue(foundCycle2);
    }

    @Test
    public void testTopologicalSortSimpleDAG() {
        Graph graph = new Graph(4, Arrays.asList(
                new Edge(0, 1, 1),
                new Edge(0, 2, 1),
                new Edge(1, 3, 1),
                new Edge(2, 3, 1)
        ), true);

        KahnsAlgorithm topoSort = new KahnsAlgorithm();
        var result = topoSort.topologicalSort(graph);

        List<Integer> order = result.getTopologicalOrder();
        assertEquals(4, order.size());

        // Verify topological order property
        assertTrue("0 should come before 1", order.indexOf(0) < order.indexOf(1));
        assertTrue("0 should come before 2", order.indexOf(0) < order.indexOf(2));
        assertTrue("1 should come before 3", order.indexOf(1) < order.indexOf(3));
        assertTrue("2 should come before 3", order.indexOf(2) < order.indexOf(3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTopologicalSortWithCycle() {
        Graph graph = new Graph(3, Arrays.asList(
                new Edge(0, 1, 1),
                new Edge(1, 2, 1),
                new Edge(2, 0, 1) // Creates cycle
        ), true);

        KahnsAlgorithm topoSort = new KahnsAlgorithm();
        topoSort.topologicalSort(graph); // Should throw exception
    }

    @Test
    public void testDAGShortestPath() {
        Graph graph = new Graph(6, Arrays.asList(
                new Edge(0, 1, 5),
                new Edge(0, 2, 3),
                new Edge(1, 3, 6),
                new Edge(1, 4, 2),
                new Edge(2, 3, 7),
                new Edge(2, 4, 4),
                new Edge(3, 5, 1),
                new Edge(4, 5, 1)
        ), true);

        // First get topological order
        KahnsAlgorithm topoSort = new KahnsAlgorithm();
        var topoResult = topoSort.topologicalSort(graph);

        // Then find the shortest paths
        DAGShortestPath pathFinder = new DAGShortestPath();
        var result = pathFinder.findShortestPaths(graph, topoResult.getTopologicalOrder(), 0);

        int[] distances = result.getDistances();
        assertEquals(0, distances[0]);
        assertEquals(5, distances[1]);
        assertEquals(3, distances[2]);
        assertEquals(10, distances[3]); // 0->2->3 = 3+7 = 10
        assertEquals(7, distances[4]);  // 0->1->4 = 5+2 = 7
        assertEquals(8, distances[5]);  // 0->1->4->5 = 5+2+1 = 8
    }

    @Test
    public void testCriticalPath() {
        Graph graph = new Graph(6, Arrays.asList(
                new Edge(0, 1, 5),
                new Edge(0, 2, 3),
                new Edge(1, 3, 6),
                new Edge(1, 4, 2),
                new Edge(2, 3, 7),
                new Edge(2, 4, 4),
                new Edge(3, 5, 1),
                new Edge(4, 5, 1)
        ), true);

        // First get topological order
        KahnsAlgorithm topoSort = new KahnsAlgorithm();
        var topoResult = topoSort.topologicalSort(graph);

        // Then find critical path
        DAGShortestPath pathFinder = new DAGShortestPath();
        var result = pathFinder.findCriticalPath(graph, topoResult.getTopologicalOrder());

        int maxDistance = result.getMaxDistance();
        List<Integer> criticalPath = result.getCriticalPath();

        // Let's analyze the possible paths:
        // Path 1: 0->1->3->5 = 5+6+1 = 12
        // Path 2: 0->1->4->5 = 5+2+1 = 8
        // Path 3: 0->2->3->5 = 3+7+1 = 11
        // Path 4: 0->2->4->5 = 3+4+1 = 8
        // The critical path (longest) should be 0->1->3->5 = 12

        assertEquals("Critical path length should be 12 (path: 0->1->3->5)", 12, maxDistance);
        assertTrue("Critical path should contain start and end vertices",
                criticalPath.contains(0) && criticalPath.contains(5));
    }

    @Test
    public void testSCCCondensationGraphIsDAG() {
        // Any graph's condensation graph should be a DAG
        Graph graph = new Graph(6, Arrays.asList(
                new Edge(0, 1, 1), new Edge(1, 0, 1), // Cycle
                new Edge(2, 3, 1), new Edge(3, 2, 1), // Cycle
                new Edge(0, 2, 1), // Connection between cycles
                new Edge(4, 5, 1)  // Simple edge
        ), true);

        TarjanSCC sccFinder = new TarjanSCC();
        var result = sccFinder.findSCCs(graph);

        Graph condensationGraph = result.getCondensationGraph();

        // The condensation graph should be a DAG, so topological sort should work
        KahnsAlgorithm topoSort = new KahnsAlgorithm();
        var topoResult = topoSort.topologicalSort(condensationGraph);

        // Should not throw exception and should return valid order
        assertNotNull(topoResult.getTopologicalOrder());
        assertEquals(condensationGraph.getVertexCount(), topoResult.getTopologicalOrder().size());
    }

    @Test
    public void testPerformanceMetrics() {
        Graph graph = new Graph(4, Arrays.asList(
                new Edge(0, 1, 1),
                new Edge(1, 2, 1),
                new Edge(2, 3, 1),
                new Edge(3, 0, 1)
        ), true);

        TarjanSCC sccFinder = new TarjanSCC();
        var result = sccFinder.findSCCs(graph);

        var metrics = result.getMetrics();
        assertTrue("Elapsed time should be non-negative", metrics.getElapsedTime() >= 0);
        assertTrue("Should record DFS visits", metrics.getOperationCount("DFS visits") > 0);
        assertTrue("Should record edge traversals", metrics.getOperationCount("Edge traversals") > 0);
    }
}
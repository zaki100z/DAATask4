package graph;

import graph.dagsp.DAGShortestPath;
import graph.topo.KahnsAlgorithm;
import graph.model.Graph;
import graph.model.Edge;
import org.junit.Test;
import java.util.Arrays;
import static org.junit.Assert.*;

public class DAGShortestPathTest {

    @Test
    public void testShortestPathUnreachableVertices() {
        Graph graph = new Graph(4, Arrays.asList(
                new Edge(0, 1, 5),
                new Edge(1, 2, 3)
                // Vertex 3 is unreachable from 0
        ), true);

        KahnsAlgorithm topoSort = new KahnsAlgorithm();
        var topoResult = topoSort.topologicalSort(graph);

        DAGShortestPath pathFinder = new DAGShortestPath();
        var result = pathFinder.findShortestPaths(graph, topoResult.getTopologicalOrder(), 0);

        int[] distances = result.getDistances();
        assertEquals(0, distances[0]);
        assertEquals(5, distances[1]);
        assertEquals(8, distances[2]);
        assertEquals(Integer.MAX_VALUE, distances[3]); // Unreachable
    }

    @Test
    public void testShortestPathMultiplePaths() {
        Graph graph = new Graph(4, Arrays.asList(
                new Edge(0, 1, 10),
                new Edge(0, 2, 3),
                new Edge(1, 3, 1),
                new Edge(2, 3, 4)
        ), true);

        KahnsAlgorithm topoSort = new KahnsAlgorithm();
        var topoResult = topoSort.topologicalSort(graph);

        DAGShortestPath pathFinder = new DAGShortestPath();
        var result = pathFinder.findShortestPaths(graph, topoResult.getTopologicalOrder(), 0);

        int[] distances = result.getDistances();
        // Shortest path: 0->2->3 = 3+4=7 (not 0->1->3=10+1=11)
        assertEquals(7, distances[3]);
    }

    @Test
    public void testCriticalPathComplex() {
        Graph graph = new Graph(7, Arrays.asList(
                new Edge(0, 1, 3),
                new Edge(0, 2, 2),
                new Edge(1, 3, 4),
                new Edge(1, 4, 2),
                new Edge(2, 4, 3),
                new Edge(2, 5, 1),
                new Edge(3, 6, 2),
                new Edge(4, 6, 3),
                new Edge(5, 6, 4)
        ), true);

        KahnsAlgorithm topoSort = new KahnsAlgorithm();
        var topoResult = topoSort.topologicalSort(graph);

        DAGShortestPath pathFinder = new DAGShortestPath();
        var result = pathFinder.findCriticalPath(graph, topoResult.getTopologicalOrder());

        // Critical path should be the longest path: 0->1->3->6 = 3+4+2=9
        // OR 0->2->5->6 = 2+1+4=7 OR 0->1->4->6 = 3+2+3=8
        // The longest is 9
        assertEquals(9, result.getMaxDistance());
    }
}
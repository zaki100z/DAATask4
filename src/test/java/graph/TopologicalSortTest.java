package graph;

import graph.topo.KahnsAlgorithm;
import graph.model.Graph;
import graph.model.Edge;
import org.junit.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.*;

public class TopologicalSortTest {

    @Test
    public void testTopologicalSortLinearGraph() {
        Graph graph = new Graph(5, Arrays.asList(
                new Edge(0, 1, 1),
                new Edge(1, 2, 1),
                new Edge(2, 3, 1),
                new Edge(3, 4, 1)
        ), true);

        KahnsAlgorithm topoSort = new KahnsAlgorithm();
        var result = topoSort.topologicalSort(graph);

        List<Integer> order = result.getTopologicalOrder();
        assertEquals(Arrays.asList(0, 1, 2, 3, 4), order);
    }

    @Test
    public void testTopologicalSortMultipleSources() {
        Graph graph = new Graph(5, Arrays.asList(
                new Edge(0, 2, 1),
                new Edge(1, 2, 1),
                new Edge(2, 3, 1),
                new Edge(2, 4, 1)
        ), true);

        KahnsAlgorithm topoSort = new KahnsAlgorithm();
        var result = topoSort.topologicalSort(graph);

        List<Integer> order = result.getTopologicalOrder();
        assertEquals(5, order.size());
        assertTrue(order.indexOf(0) < order.indexOf(2));
        assertTrue(order.indexOf(1) < order.indexOf(2));
        assertTrue(order.indexOf(2) < order.indexOf(3));
        assertTrue(order.indexOf(2) < order.indexOf(4));
    }

    @Test
    public void testTopologicalSortSingleNode() {
        Graph graph = new Graph(1, Arrays.asList(), true);

        KahnsAlgorithm topoSort = new KahnsAlgorithm();
        var result = topoSort.topologicalSort(graph);

        assertEquals(Arrays.asList(0), result.getTopologicalOrder());
    }
}
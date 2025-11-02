package graph.metrics;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class Metrics {
    private long startTime;
    private long endTime;
    private Map<String, Integer> operationCounts;

    public Metrics() {
        this.operationCounts = new ConcurrentHashMap<>();
    }

    public void startTimer() {
        this.startTime = System.nanoTime();
    }

    public void stopTimer() {
        this.endTime = System.nanoTime();
    }

    public long getElapsedTime() {
        return endTime - startTime;
    }

    public void incrementOperation(String operation) {
        operationCounts.merge(operation, 1, Integer::sum);
    }

    public int getOperationCount(String operation) {
        return operationCounts.getOrDefault(operation, 0);
    }

    public Map<String, Integer> getAllOperationCounts() {
        return new HashMap<>(operationCounts);
    }

    public void reset() {
        operationCounts.clear();
        startTime = 0;
        endTime = 0;
    }
}
package graph.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Edge {
    @JsonProperty("u")
    private int u;

    @JsonProperty("v")
    private int v;

    @JsonProperty("w")
    private int w;

    // Default constructor for Jackson
    public Edge() {}

    public Edge(int u, int v, int w) {
        this.u = u;
        this.v = v;
        this.w = w;
    }

    // Getters and setters
    public int getU() { return u; }
    public int getV() { return v; }
    public int getW() { return w; }

    public void setU(int u) { this.u = u; }
    public void setV(int v) { this.v = v; }
    public void setW(int w) { this.w = w; }

    @Override
    public String toString() {
        return "Edge{u=" + u + ", v=" + v + ", w=" + w + '}';
    }
}
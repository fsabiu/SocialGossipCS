package server;

import java.util.*;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @author Marco Cardia
 * @author Francesco Sabiu
 * The class Graph represents a generic type E undirected graph
 * @param <E>
 */
public class Graph<E> {
	final private ConcurrentHashMap<E, Set<E>> adjacencyList;
	
    public Graph() {
        this.adjacencyList = new ConcurrentHashMap<>();
    }
    
    /**
     * Add new vertex to the graph.
     * 
     * @param e The vertex object. 
     */
    public void addVertex(E e) {
        if (this.adjacencyList.containsKey(e)) {
            throw new IllegalArgumentException("Vertex already exists.");
        }
        this.adjacencyList.put(e, new HashSet<E>());
    }
	
    /**
     * Remove the vertex v from the graph.
     * 
     * @param e The vertex that will be removed.
     */
    public void removeVertex(E e) {
        if (!this.adjacencyList.containsKey(e)) {
            throw new IllegalArgumentException("Vertex doesn't exist.");
        }
        
        this.adjacencyList.remove(e);
        
        for (E v: this.getAdjVertices(e)) {
            this.adjacencyList.get(v).remove(e);
        }
    }
    
    /**
     * Adds an edge between the vertices e1 and e2
     * @param e1 Vertex
     * @param e2 Vertex
     */
	public void addEdge(E e1, E e2) {
        if (!this.adjacencyList.containsKey(e1) || !this.adjacencyList.containsKey(e2)) {
            throw new IllegalArgumentException();
        }
        
        this.adjacencyList.get(e1).add(e2);
        this.adjacencyList.get(e2).add(e1);
	}
	
	/**
	 * It removes the edge between the vertices e1 and e2, if it exists.
	 * @param v
	 * @param u
	 */
	public void removeEdge(E e1, E e2) {
		if (!this.adjacencyList.containsKey(e1) || !this.adjacencyList.containsKey(e2)) {
            throw new IllegalArgumentException();
        }
        
        this.adjacencyList.get(e1).remove(e2);
        this.adjacencyList.get(e2).remove(e1);
    }
	
    /**
     * It returns all the vertices in the graph.
     * 
     * @return An iterable for all vertices in the graph.
     */
    public Iterable<E> getVertices() {
        return this.adjacencyList.keySet();
    }
    
    /**
     * It returns the set of the adjacent vertices of a given vertex.
     * @param e
     * @return
     */
    public Set<E> getAdjVertices(E e) {
        return this.adjacencyList.get(e);
    }
    
}

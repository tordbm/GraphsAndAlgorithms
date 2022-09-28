package student;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Set;

import graph.Edge;
import graph.Graph;
import graph.WeightedGraph;

public class ProblemSolver implements IProblem {

	@Override
	public <T, E extends Comparable<E>> ArrayList<Edge<T>> mst(WeightedGraph<T, E> g) {
		// Task 1
		ArrayList<Edge<T>> chosen = new ArrayList<>();
		HashSet<T> touched = new HashSet<>();
		T startNode = g.vertices().iterator().next(); //O(1)

		PriorityQueue<Edge<T>> queue = new PriorityQueue<>(g);

		updateBfs(g, queue, touched, startNode); //O(degree)

		while (!queue.isEmpty()) { //m times //O(m(log(n)))
			Edge<T> ed = queue.poll(); //O(log(n))
			T touchedNode = getTouchedNode(ed, touched);
			T newNode = ed.other(touchedNode);
			if (touched.contains(newNode))
				continue;

			chosen.add(ed);
			updateBfs(g, queue, touched, newNode); // O(m)
		}

		return chosen;
	}
	
	
	/** Method used to update the various collections thats keeping track of things in the algorithm
	 * @param <T> - The type of vertices
	 * @param <E> - The type of Edge weights
	 * @param g - the weighted graph
	 * @param pq - the priority queue of edges
	 * @param touched - a hashSet thats keeping track of nodes that have been visited or "touched"
	 * @param newNode - the new node that the edge is pointing to
	 */
	private static <T, E extends Comparable<E>> void updateBfs(WeightedGraph<T, E> g, PriorityQueue<Edge<T>> pq, HashSet<T> touched, T newNode) {
		touched.add(newNode); //O(1)
		for(Edge<T> edge : g.adjacentEdges(newNode)) { //O(m)
			if(touched.contains(edge.a) && touched.contains(edge.b)) { //O(1)
				continue;
			}
			pq.add(edge); //O(1)
		}
	}

	/**
	 * @param <T> - The type of vertices
	 * @param e - the edge to be checked
	 * @param touched - a hashSet thats keeping track of nodes that have been visited or "touched"
	 * @return the touched node
	 */
	private static <T> T getTouchedNode(Edge<T> e, HashSet<T> touched) { //O(1)
		if(touched.contains(e.a))
			return e.a;
		if(touched.contains(e.b))
			return e.b;

		throw new IllegalArgumentException("e should always have one endpoint in found");
	}
	


	@Override
	public <T> T lca(Graph<T> g, T root, T u, T v) { //O(n*degree)
		// Task 2
		LinkedList <T> dfsU = getPath(u, (dfs (u, g, root)), root); //O(n + (n * degree))
		LinkedList <T> dfsV = getPath(v, (dfs (v, g, root)), root); //O(n + (n * degree))
		HashSet <T> dfsVSet = new HashSet<>();
		dfsVSet.addAll(dfsV); //O(n)
		T lca = null;
		
		for(T node : dfsU) { //O(n) worst case if root is lca, expected less as root rarely is the lca
			if (dfsVSet.contains(node)) {//O(1)
				lca = node;
				break;
			}
		}
		return lca;
		
	}
	
	
	/**
	 * Method for running a depth first search in a given, undirected graph
	 * 
	 * @param <T> - The type of verticies
	 * @param endNode - The node that will end the search
	 * @param g - The graph
	 * @param root - The root/powerstation
	 * @return a child -> parent HashMap with nodes as keys pointing to their parent
	 */
	private <T> HashMap <T, T>  dfs (T endNode, Graph<T> g, T root) {
		
		LinkedList<T> toSearch = new LinkedList<T>();
		HashSet<T> touched = new HashSet<T>();
		HashMap<T, T> route = new HashMap<>();
		int numVert = g.numVertices(); //O(1)
		//If endNode == null then use second condition, else first. To make dfs method usable without a given end node
		boolean endCondition = endNode == null ? !touched.contains(endNode) : touched.size() < numVert;

		updateDfs(g, touched, toSearch, route, root); //O(degree)

		while (!toSearch.isEmpty() && endCondition) { // O(n)
			T newNode = toSearch.removeLast();
			if (touched.contains(newNode)) 
				continue;
			
			updateDfs(g, touched, toSearch, route, newNode); //O(n)
		}
		return route;
	}

	/**
	 * Method for retrieving a wanted subtree from a graph
	 * 
	 * @param <T> - The type of verticies
	 * @param endNode - The goal node
	 * @param route - A child -> parent HashMap with nodes as keys pointing to their parent
	 * @param root - The root
	 * @return a LinkedList of the subtree from endNode to root
	 */
	private <T> LinkedList<T> getPath (T endNode, HashMap <T, T> route, T root){ //O(n)
		
		LinkedList<T> path = new LinkedList<>();
		boolean done = false;
		T node = endNode;
		while (!done) { //O(n) depends on the number of nodes in the tree
			if (node.equals(root)) //O(1)
				done = true;
			path.add(node); //O(1)
			node = route.get(node);//O(1)
		}
		return path;
	}
	
	
	/** Method used to update the various collections thats keeping track of things in the algorithm
	 * @param <T> - The type of vertices
	 * @param g - the graph
	 * @param toSearch - the list if nodes to be searched
	 * @param touched - a hashSet thats keeping track of nodes that have been visited or "touched"
	 * @param route - a child -> parent hashMap
	 * @param newNode - the new node to be used for search
	 */
	private static <T> void updateDfs (Graph<T> g, HashSet<T> touched, LinkedList<T> toSearch, HashMap<T, T> route, T newNode) {
		touched.add(newNode);
			for(T node : g.neighbours(newNode)) { //O(degree)
				if(touched.contains(node)) { //O(1)
					continue;
				}
				toSearch.addLast(node);
				route.put(node, newNode); //O(1)
		}
	}


	@Override
	public <T> Edge<T> addRedundant (Graph<T> g, T root) {
		// Task 3
		HashMap <T, T> allNodes = dfs(null, g, root);
		Set<T> keys = allNodes.keySet();
		HashMap <Integer, LinkedList<T>> subTree = new HashMap<>();
		
		for (T key : keys) {
			LinkedList <T> tree = getPath(key, allNodes, root);
			subTree.put(tree.size(), tree);
		}
		int max = Collections.max(subTree.keySet());
		LinkedList <T> largest = subTree.get(max);
		subTree.remove(max);
		max = Collections.max(subTree.keySet());
		LinkedList <T> scndLargest = subTree.get(max);
		
		Edge<T> newEdge = new Edge<T>(largest.getFirst(), scndLargest.getFirst());
		
		return newEdge;
	}
}

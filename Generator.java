package generator;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;

import graph.*;
import student.*;

public class Generator {
	
	public static void main(String[] args) throws IOException{
		long seed = 0L;
		System.out.println("generate MST instances");
		for (int i = 0; i < 5; i++) {
			seed = 1234567 * seed + 9876543;
			new Generator(seed).generateMSTProblem(1000, 10000, "input/MST"+i);
		}
		System.out.println("generate LCA instances");
		for (int i = 0; i < 5; i++) {
			seed = 1234567 * seed + 9876543;
			new Generator(seed).generateLCAProblem(1000, 10, "input/LCA"+i);
		}
		System.out.println("generate REP instances");
		for (int i = 0; i < 5; i++) {
			seed = 1234567 * seed + 9876543;
			new Generator(seed).generateRedundantEdgeProblem(10000, "input/REP"+i);
		}
	}
	
	public Generator(long seed){
		random.setSeed(seed);
	}
	
	Random random = new Random();
	private final int MAX_W = 1000; 
	
	/**
	 * generates a random tree with n vertices
	 * 
	 * @param n
	 * @param seed
	 * @return
	 */
	public Graph<Integer> generateTree(int n){
		Graph<Integer> g = new Graph<Integer>();
		
		// SHUFFLE VERTICES
		int[] ids = new int[n];
		for (int i = 0; i < n; i++) ids[i] = i;
		for (int i = 1; i < n; i++) {
			int j = random.nextInt(i+1);
			ids[i] ^= ids[j];
			ids[j] ^= ids[i];
			ids[i] ^= ids[j];
		}
		
		for (int u = 0; u < n; u++) g.addVertex(u);
		for (int i = 1; i < n; i++) {
			int u = random.nextInt(i);
			int v = i;
			
			g.addEdge(u, v);
		}
		
		return g;
	}
	
	/**
	 * generates a random WeightedGraph with n vertices and m edges, expected runtime is ~n + m log m when
	 * m >>>> n, dont't use for dense graphs
	 * 
	 * @param n
	 * @param m
	 * @param seed
	 * @return
	 */
	private WeightedGraph<Integer, Integer> generateWeightedGraph(int n, int m){
		Graph<Integer> tmp = generateTree(n);
		WeightedGraph<Integer, Integer> g = new WeightedGraph<Integer, Integer>();
		for (Integer u: tmp.vertices()) g.addVertex(u); 
		for (Edge e: tmp.edges()) g.addEdge((Integer) e.a, (Integer) e.b, random.nextInt(MAX_W));
		for (int i = n-1; i < m; i++) {
			int u = random.nextInt(n);
			int v = random.nextInt(n);
			if (g.adjacent(u, v) || u == v) {
				i--;
				continue;
			}
			g.addEdge(u, v, random.nextInt(MAX_W));
		}
		
		return g;
	}
	
	public void generateMSTProblem(int n, int m, String name) throws IOException{
		WeightedGraph<Integer, Integer> g = generateWeightedGraph(n, m);
		
		//print problem
		BufferedWriter bf = new BufferedWriter(new FileWriter(new File(name + ".in")));
		bf.write(String.format(Locale.US, "%d %d\n", n, m));
		for (Edge<Integer> edge: g.edges()) {
			bf.write(String.format(Locale.US, "%d %d %d\n", edge.a, edge.b, g.getWeight(edge.a, edge.b)));
		}
		bf.close();
		
		//print ans
		bf = new BufferedWriter(new FileWriter(new File(name + ".ans")));
		ArrayList<Edge<Integer>> edges = new ProblemSolver().mst(g);
		int sum = 0;
		for (Edge<Integer> edge: edges) sum += g.getWeight(edge.a, edge.b);
		bf.write(String.format(Locale.US, "%d\n", sum));
		bf.close();
	}
	
	public void generateLCAProblem(int n, int q, String name) throws IOException{
		Graph<Integer> g = generateTree(n);
		ArrayList<Edge<Integer>> queries = new ArrayList<Edge<Integer>>();
		int root = 0;
		for (int i = 0; i < q; i++) {
			int u = random.nextInt(n);
			int v = random.nextInt(n);
			if (u == v) {
				i--;
				continue;
			}
			queries.add(new Edge<Integer>(u, v));
		}
		
		//print problem
		BufferedWriter bf = new BufferedWriter(new FileWriter(new File(name + ".in")));
		bf.write(String.format(Locale.US, "%d %d\n", n, q));
		for (Edge<Integer> edge: g.edges()) {
			bf.write(String.format(Locale.US, "%d %d\n", edge.a, edge.b));
		}
		for (Edge<Integer> edge: queries) bf.write(String.format(Locale.US, "%d %d\n", edge.a, edge.b));
		bf.close();
		
		//print answer
		bf =  new BufferedWriter(new FileWriter(new File(name + ".ans")));
		for (Edge<Integer> edge: queries) bf.write(String.format(Locale.US, "%d\n", new ProblemSolver().lca(g, 0, edge.a, edge.b)));
		bf.close();
	}
	
	public void generateRedundantEdgeProblem(int n, String name) throws IOException{
		Graph<Integer> g = generateTree(n);
		
		//print problem
		BufferedWriter bf = new BufferedWriter(new FileWriter(new File(name + ".in")));
		bf.write(String.format(Locale.US, "%d\n", n));
		for (Edge<Integer> edge: g.edges()) {
			bf.write(String.format(Locale.US, "%d %d\n", edge.a, edge.b));
		}
		bf.close();
		
		//print ans
		bf = new BufferedWriter(new FileWriter(new File(name + ".ans")));
		Edge<Integer> ans = new ProblemSolver().addRedundant(g,0);
		bf.write(String.format(Locale.US, "%d %d\n", ans.a, ans.b));
		bf.close();
	}
}

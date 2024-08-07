package test_GS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.SingleGraph;

public class Assignment4 {

	
	public static List<Integer> getShortestPath(Graph graph, Node startNode, Node endNode) {
		int n = graph.getNodeCount();
		double[] D = new double[n];
		int[] previous = new int[n];
		boolean[] visited = new boolean[n];

		for (int i = 0; i < n; i++) {
			D[i] = Double.POSITIVE_INFINITY;
			previous[i] = -1;
		}

		D[startNode.getIndex()] = 0;

		PriorityQueue<Node> pq = new PriorityQueue<Node>((o1, o2) -> Double.compare(D[o1.getIndex()], D[o2.getIndex()]));

		pq.offer(startNode);

		while (!pq.isEmpty()) {
			Node u = pq.poll();
			if (visited[u.getIndex()]) {
				continue;
			}
			visited[u.getIndex()] = true;

			int deg = u.getDegree();
			for (int i = 0; i < deg; i++) {
				Edge e = u.getEdge(i);
				Node v = e.getOpposite(u);
				double weight = (double) e.getAttribute("weight");
				if (D[u.getIndex()] + weight < D[v.getIndex()]) {
					D[v.getIndex()] = D[u.getIndex()] + weight;
					previous[v.getIndex()] = u.getIndex();
					v.setAttribute("prev", u.getId());
					pq.offer(v);
				}
			}
		}

		List<Integer> path = new ArrayList<>();
		Node node = endNode;
		while (node != null) {
			int id = Integer.parseInt(node.getId());
			path.add(id);
			Object prevAttr = node.getAttribute("prev");
			Node prev1 = prevAttr != null ? graph.getNode(prevAttr.toString()) : null;
			if (prev1 != null) {
				Edge edge = prev1.getEdgeBetween(node);
				if (edge != null) {
					edge.setAttribute("highlight", true);
					prev1.setAttribute("highlight", true);
					node.setAttribute("highlight", true);
				}
			}
			node = prev1;
		}
		Collections.reverse(path);
		return path;
	}
	public static void main(String[] args) throws IOException {
		File file = new File("graph.txt");
		Graph graph = new SingleGraph("Random", true, true);
		System.setProperty("org.graphstream.ui", "swing");

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line = "";
			while ((line = reader.readLine()) != null) {
				String[] data = line.split("\\s+");
				String first = data[0];
				if (graph.getNode(first) == null) {
					graph.addNode(first).setAttribute("ui.label", first);
				}
				for (int i = 1; i < data.length; i++) {
					String end = data[i];
					if (graph.getNode(end) == null) {
						graph.addNode(end).setAttribute("ui.label", end);
					}
					if (!graph.getNode(first).hasEdgeToward(end)) {
						Edge edges = graph.addEdge(first + "_" + end, first, end);
						double weight = 1;
						edges.setAttribute("weight", weight);
					}
				}
			}
		}

		for (Node node : graph) {
			System.out.print(node.getId() + ": ");
			int deg = node.getDegree();
			for (int i = 0; i < deg; i++) {
				Edge edge = node.getEdge(i);
				Node neighbors = edge.getOpposite(node);
				System.out.print(neighbors.getId() + " ");
			}
			System.out.println();
		}

		for (Node node : graph) {
			node.setAttribute("ui.label", node.getId());
			node.setAttribute("ui.style", "shape: circle; size: 30px; fill-color: red; stroke-mode: plain; stroke-color: black; text-size: 20px; text-alignment: center; text-color: black;");
		}


		graph.display();
		Scanner scanner = new Scanner(System.in);
		boolean exit = false;
		do {
			System.out.println("Enter starting node or -1 to exit: ");
			int start = scanner.nextInt();
			if (start == -1) {
				System.out.println("Exiting");
				exit = true;
				break;
			}

			System.out.println("Enter ending node: ");
			int end = scanner.nextInt();

			Node startNode = graph.getNode(String.valueOf(start));
			Node endNode = graph.getNode(String.valueOf(end));
			List<Integer> path = getShortestPath(graph,startNode,endNode);
			for (Node node : graph) {
				node.setAttribute("prev", -1);
				node.setAttribute("ui.label", node.getId());
			}
			for (int i = 0; i < path.size() - 1; i++) {
	            int id1 = path.get(i);
	            int id2 = path.get(i + 1);
	            Node node1 = graph.getNode(String.valueOf(id1));
	            Node node2 = graph.getNode(String.valueOf(id2));
	            Edge edge = node1.getEdgeBetween(node2);
	            edge.setAttribute("ui.style", "fill-color: red; size: 3px;");
	        }
			System.out.println(path);
		} while (!exit);
		graph.display();
		System.out.println();
		scanner.close();

	}

}

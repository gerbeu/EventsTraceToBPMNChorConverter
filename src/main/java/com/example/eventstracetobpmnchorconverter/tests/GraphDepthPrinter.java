package com.example.eventstracetobpmnchorconverter.tests;

import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;

import java.util.ArrayDeque;
import java.util.Queue;

public class GraphDepthPrinter {

    public static <N> void printTree(Graph<N> graph, N root) {
        Queue<N> queue = new ArrayDeque<>();
        queue.add(root);
        int level = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();
            System.out.println("Level " + level + ": ");
            for (int i = 0; i < size; i++) {
                N node = queue.poll();
                System.out.print(node + " ");
                for (N neighbor : graph.successors(node)) {
                    queue.add(neighbor);
                }
            }
            System.out.println();
            level++;
        }
    }

    public static void main(String[] args) {
        MutableGraph<Integer> graph = GraphBuilder.directed().build();
        graph.putEdge(1, 2);
        graph.putEdge(1, 3);
        graph.putEdge(2, 4);
        graph.putEdge(2, 5);
        graph.putEdge(3, 6);
        graph.putEdge(3, 7);
        printTree(graph, 1);
    }

}

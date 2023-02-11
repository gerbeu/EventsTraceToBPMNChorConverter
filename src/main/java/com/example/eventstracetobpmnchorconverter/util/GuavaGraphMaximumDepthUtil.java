package com.example.eventstracetobpmnchorconverter.util;

import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;

import java.util.ArrayDeque;
import java.util.Queue;

public class GuavaGraphMaximumDepthUtil {

    private static int maxDepth = 0;

    public static <N> int getMaximumDepth(Graph<N> graph, N node, int depth) {
        maxDepth = Math.max(depth, maxDepth);
        for (N child : graph.successors(node)) {
            getMaximumDepth(graph, child, depth + 1);
        }
        return maxDepth;
    }

    public static <N> int getMaximumDepth(Graph<N> graph, N node) {
        maxDepth = 0;
        return getMaximumDepth(graph, node, 0);
    }

    public static <N> int getDepth(Graph<N> graph, N node) {
        Queue<N> queue = new ArrayDeque<>();
        queue.add(node);
        int depth = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                N current = queue.poll();
                for (N neighbor : graph.successors(current)) {
                    queue.add(neighbor);
                }
            }
            depth++;
        }
        return depth - 1;
    }

    public static <N> void printNodesByDepth(Graph<N> graph) {
        System.out.println("BEGINNING OF PRINTING NODES BY DEPTH");
        int maxDepth = 0;
        for (N node : graph.nodes()) {
            maxDepth = Math.max(maxDepth, getDepth(graph, node));
        }

        for (int depth = 1; depth <= maxDepth; depth++) {
            System.out.print("Nodes at depth " + depth + ": ");
            for (N node : graph.nodes()) {
                if (getDepth(graph, node) == depth) {
                    System.out.print(node + " ");
                }
            }
            System.out.println();
        }
        System.out.println("END OF PRINTING NODES BY DEPTH");
    }


    public static void main(String[] args) {
        MutableGraph<Integer> graph = GraphBuilder.directed().build();
        graph.addNode(1);
        graph.addNode(2);
        graph.addNode(3);
        graph.addNode(4);
        graph.putEdge(1, 2);
        graph.putEdge(2, 3);
        graph.putEdge(3, 4);
        int maxDepth = getMaximumDepth(graph, 1);
        System.out.println("The maximum depth of the graph is: " + maxDepth);
    }

}

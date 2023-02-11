package com.example.eventstracetobpmnchorconverter.tests;

import com.google.common.graph.Graph;
import com.google.common.graph.MutableGraph;

import java.util.Set;

public class NodeChildrenPrinter {
    public static <N> void printChildren(Graph<N> graph, N node) {
        Set<N> children = graph.successors(node);
        System.out.println("Children of node " + node + ": " + children);
    }

    public static void main(String[] args) {
        MutableGraph<Integer> graph = com.google.common.graph.GraphBuilder.directed().build();
        graph.putEdge(1, 2);
        graph.putEdge(1, 3);
        graph.putEdge(2, 4);
        graph.putEdge(2, 5);
        graph.putEdge(3, 6);
        graph.putEdge(3, 7);
        printChildren(graph, 3);
    }
}

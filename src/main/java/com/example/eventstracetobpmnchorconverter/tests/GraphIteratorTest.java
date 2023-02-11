package com.example.eventstracetobpmnchorconverter.tests;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;

public class GraphIteratorTest {


    public static void main(String[] args) {
        MutableGraph<String> graph = GraphBuilder.directed().build();
        graph.putEdge("api", "produced event orderCreated");
        graph.putEdge("produced event orderCreated", "orders send");
        graph.putEdge("orders send", "reefer orders receive");
        graph.putEdge("orders send", "voyage orders receive");
        graph.putEdge("reefer orders receive", "reefer processed event[OrderCreated]");
        graph.putEdge("voyage orders receive", "voyage processed event[OrderCreated]");
        printGraphUsingIterator(graph, 1);
    }

    private static void printGraphUsingIterator(MutableGraph<String> graph, int i) {
        final var graphIterator = graph.nodes().iterator();
        while (graphIterator.hasNext()) {
            final var node = graphIterator.next();
            System.out.println(node);
        }
    }
}

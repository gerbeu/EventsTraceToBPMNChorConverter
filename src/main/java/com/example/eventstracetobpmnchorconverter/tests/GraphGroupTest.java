package com.example.eventstracetobpmnchorconverter.tests;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.HashSet;
import java.util.Set;

public class GraphGroupTest {
    private static Multimap<String, String> graph = HashMultimap.create();

    public static void main(String[] args) {
        graph.put("i", "x_produce_1");
        graph.put("x_produce_1", "x_publish_1");
        graph.put("x_publish_1", "b_receive_1");
        graph.put("x_publish_1", "c_receive_1");
        graph.put("b_receive_1", "b_process_1");
        graph.put("c_receive_1", "c_process_1");
        graph.put("b_process_1", "b_produce_2");
        graph.put("b_produce_2", "b_publish_2");

        Set<String> visitedNodes = new HashSet<>();
        for (String node : graph.keySet()) {
            if (!visitedNodes.contains(node)) {
                visitedNodes.add(node);
                Set<String> prodNodes = new HashSet<>();
                Set<String> processNodes = new HashSet<>();
                for (String neighbor : graph.get(node)) {
                    if (neighbor.contains("produce")) {
                        prodNodes.add(neighbor);
                        visitedNodes.add(neighbor);
                    }
                    if (neighbor.contains("process")) {
                        processNodes.add(neighbor);
                        visitedNodes.add(neighbor);
                    }
                }

                if (!prodNodes.isEmpty() && !processNodes.isEmpty()) {
                    String prodGroup = "prod_group_" + node;
                    String processGroup = "process_group_" + node;
                    graph.removeAll(node);
                    graph.putAll(prodGroup, prodNodes);
                    graph.putAll(processGroup, processNodes);
                    graph.put(node, prodGroup);
                    graph.put(node, processGroup);
                }
            }
        }

        System.out.println("New graph: " + graph);
    }
}

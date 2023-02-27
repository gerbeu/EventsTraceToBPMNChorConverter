package com.example.eventstracetobpmnchorconverter.layout;

import com.google.common.graph.Graph;

public interface GraphLayoutCreator<N> {

    void layout(Graph<N> graph, N root);
}

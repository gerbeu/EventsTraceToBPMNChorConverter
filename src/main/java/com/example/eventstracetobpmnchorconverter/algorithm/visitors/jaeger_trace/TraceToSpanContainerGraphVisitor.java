package com.example.eventstracetobpmnchorconverter.algorithm.visitors.jaeger_trace;

import com.example.eventstracetobpmnchorconverter.jaegerTrace.Trace;
import com.example.eventstracetobpmnchorconverter.producing.graph.SpanContainer;
import com.example.eventstracetobpmnchorconverter.util.SpanContainerGraphUtils;
import com.example.eventstracetobpmnchorconverter.util.SpanContainerUtils;
import com.example.eventstracetobpmnchorconverter.util.SpanUtils;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;
import com.google.common.graph.MutableGraph;

public class TraceToSpanContainerGraphVisitor implements TraceVisitor<ImmutableGraph<SpanContainer>> {


    @Override
    public ImmutableGraph<SpanContainer> visit(Trace trace) {
        final var spanList = trace.getSpans();
        final MutableGraph<SpanContainer> spanContainerGraph = GraphBuilder.directed().build();
        // TODO remove
        System.out.println("Spans which dont have messaging.system or messaging.destination tags:");
        spanList.stream()
                // Filter spans which dont have messaging.system or messaging.destination tags
                .filter(span -> !SpanUtils.isSpanAnMessagingMarker(span))
                .map(span -> SpanContainerUtils.createSpanContainer(span, spanList))
                .forEach(spanContainerGraph::addNode);
        // Add edges to span graph
        addEdgesToSpanContainerGraph(spanContainerGraph);
        return ImmutableGraph.copyOf(spanContainerGraph);
    }

    private void addEdgesToSpanContainerGraph(MutableGraph<SpanContainer> spanContainerGraph) {
        for (final var spanContainer : spanContainerGraph.nodes()) {
            System.out.println("Creating edge for spanContainer: " + spanContainer);
            final var children = SpanContainerGraphUtils.getSpanContainerChildrenOfSpanContainer(spanContainer,
                    spanContainerGraph);
            for (final var child : children) {
                spanContainerGraph.putEdge(spanContainer, child);
            }
        }
    }
}

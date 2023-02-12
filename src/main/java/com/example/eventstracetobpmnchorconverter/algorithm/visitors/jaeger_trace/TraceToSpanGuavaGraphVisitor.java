package com.example.eventstracetobpmnchorconverter.algorithm.visitors.jaeger_trace;

import com.example.eventstracetobpmnchorconverter.jaegerTrace.Trace;
import com.example.eventstracetobpmnchorconverter.jaegerTrace.spans.Span;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;
import com.google.common.graph.MutableGraph;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class TraceToSpanGuavaGraphVisitor implements TraceVisitor<ImmutableGraph<Span>> {


    @Override
    public ImmutableGraph<Span> visit(Trace trace) {
        final var spanList = trace.getSpans();
        final MutableGraph<Span> spanGraph = GraphBuilder.directed().build();
        // Add nodes to span graph
        System.out.println("PRINTING SPANLIST");
        for (final var span : spanList) {
            System.out.println(MessageFormat.format("Span: {0} - {1}", span.getSpanID(), span.getOperationName()));
        }
        System.out.println("END PRINTING SPANLIST");
        spanList.forEach(spanGraph::addNode);
        // Add edges to span graph
        addEdgesToSpanGraph(spanGraph, spanList);
        return ImmutableGraph.copyOf(spanGraph);
    }


    private void addEdgesToSpanGraph(MutableGraph<Span> spanGraph, List<Span> spanList) {
        for (final var span : spanList) {
            final var children = getSpanChildrenOfSpan(span, spanList);
            for (final var child : children) {
                spanGraph.putEdge(span, child);
            }
        }
    }

    private List<Span> getSpanChildrenOfSpan(Span span, List<Span> spanList) {
        final var children = new ArrayList<Span>();
        final var spanID = span.getSpanID();
        for (final var potentialChildSpan : spanList) {
            final var potentialChildSpanHasReferenceChildOf = potentialChildSpan.getReferences().stream()
                    .anyMatch(reference -> reference.getRefType().equals("CHILD_OF"));
            if (potentialChildSpanHasReferenceChildOf) {
                final var parentSpanID = potentialChildSpan.getReferences().get(0).getSpanID();
                if (spanID.equals(parentSpanID)) {
                    children.add(potentialChildSpan);
                }
            }
        }
        return children;
    }


}

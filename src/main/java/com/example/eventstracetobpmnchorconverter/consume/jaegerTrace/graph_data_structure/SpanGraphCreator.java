package com.example.eventstracetobpmnchorconverter.consume.jaegerTrace.graph_data_structure;

import com.example.eventstracetobpmnchorconverter.consume.jaegerTrace.spans.Span;

import java.util.List;

public class SpanGraphCreator {

    public SpanGraph createSpanGraph(final List<Span> spanList) {
        final SpanGraph spanGraph = new SpanGraph();
        for (final Span span : spanList) {
            SpanNode spanNode = new SpanNode(span);
            spanGraph.addSpanNode(spanNode);
        }
        for (final Span span : spanList) {
            final var spanHasReferenceChildOf = span.getReferences().stream().anyMatch(reference -> reference.getRefType().equals("CHILD_OF"));
            if(spanHasReferenceChildOf) {
                final var parentSpan =
                        spanList.stream().filter(span1 -> span1.getSpanID().equals(span.getReferences().get(0).getSpanID())).findFirst().get();
                final var parentSpanNode = spanGraph.getSpanNodeBySpan(parentSpan);
                final var spanNode = spanGraph.getSpanNodeBySpan(span);
                spanGraph.addEdge(parentSpanNode, spanNode);
            }
        }
        return spanGraph;
    }

}

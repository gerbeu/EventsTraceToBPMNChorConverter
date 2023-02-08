package com.example.eventstracetobpmnchorconverter.consume.jaegerTrace.graph_data_structure;

import com.example.eventstracetobpmnchorconverter.consume.jaegerTrace.spans.Span;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
public class SpanGraph {

    private final List<SpanNode> spanNodes;

    public SpanGraph() {
        spanNodes = new ArrayList<>();
    }

    public void addSpanNode(SpanNode spanNode) {
        spanNodes.add(spanNode);
    }

    public void addEdge(SpanNode sourceSpanNode, SpanNode destinationSpanNode) {
        sourceSpanNode.getChildSpanNodes().add(destinationSpanNode);
    }

    public SpanNode getSpanNodeBySpan(Span span) {
        for (SpanNode spanNode : spanNodes) {
            if (spanNode.getSpan().equals(span)) {
                return spanNode;
            }
        }
        return null;
    }

    public void print() {
        for (SpanNode spanNode : spanNodes) {
            System.out.println(spanNode);
        }
    }
}

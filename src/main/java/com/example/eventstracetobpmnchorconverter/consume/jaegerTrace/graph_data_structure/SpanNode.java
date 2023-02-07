package com.example.eventstracetobpmnchorconverter.consume.jaegerTrace.graph_data_structure;

import com.example.eventstracetobpmnchorconverter.consume.jaegerTrace.spans.Span;
import lombok.Getter;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Getter
public class SpanNode {

    private final Span span;

    private final List<SpanNode> childSpanNodes;

    public SpanNode(Span span) {
        this.span = span;
        childSpanNodes = new ArrayList<>();
    }

    @Override
    public String toString() {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(MessageFormat.format("SpanNode: {0} - {1}", span.getSpanID(), span.getOperationName()));
        if (childSpanNodes.isEmpty()) {
            return stringBuilder.toString();
        }
        stringBuilder.append(" | has childSpanNodes: ");
        for (SpanNode childSpanNode : childSpanNodes) {
            stringBuilder.append(MessageFormat.format("SpanNode: {0} & {1} & {2}", childSpanNode.getSpan().getSpanID(),
                    childSpanNode.getSpan().getOperationName(), childSpanNode.getSpan().getProcessID()));
            if (childSpanNode != childSpanNodes.get(childSpanNodes.size() - 1))
                stringBuilder.append(" - ");
        }
        return stringBuilder.toString();
    }

}

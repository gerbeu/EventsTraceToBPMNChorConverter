package com.example.eventstracetobpmnchorconverter.util;

import com.example.eventstracetobpmnchorconverter.jaegerTrace.spans.Span;
import com.example.eventstracetobpmnchorconverter.producing.graph.SpanContainer;
import com.example.eventstracetobpmnchorconverter.producing.graph.SpanEventTuple;
import com.google.common.graph.ImmutableGraph;
import com.google.common.graph.MutableGraph;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SpanContainerGraphUtils {

    /**
     * Search for span container children
     * If the span container holds only a span, then check if it has a ref on the spancontainer , if the
     * spancontainer is a span then the span if it is a tuple then the second span
     * Check if inside spancontainer is a span
     *
     * @param spanContainer
     * @param spanContainerGraph
     * @return
     */
    public static List<SpanContainer> getSpanContainerChildrenOfSpanContainer(SpanContainer spanContainer,
                                                                  MutableGraph<SpanContainer> spanContainerGraph) {
        final var spanContainerNodes = spanContainerGraph.nodes();

        final var isSpanContainerSpan = spanContainer.getSpanComponent() instanceof Span;
        if (isSpanContainerSpan) {
            final var span = (Span) spanContainer.getSpanComponent();
            final var spanContainerChildrenOfSpan = getSpanContainerChildrenOfSpan(span, spanContainerNodes);
            return spanContainerChildrenOfSpan;
        }
        final var isSpanContainerSpanEventTuple = spanContainer.getSpanComponent() instanceof SpanEventTuple;
        if (isSpanContainerSpanEventTuple) {
            final var spanEventTuple = (SpanEventTuple) spanContainer.getSpanComponent();
            final var spanEventTupleContainsProcessorMarkerSpan = SpanEventTupleUtil.containsSpanEventTupleProcessorMarkerSpan(spanEventTuple);
            if(spanEventTupleContainsProcessorMarkerSpan) {
                final var spanToUseForGettingChildren = spanEventTuple.getFirstSpan();
                final var spanContainerChildrenOfSpan = getSpanContainerChildrenOfSpan(spanToUseForGettingChildren,
                        spanContainerNodes);
                return spanContainerChildrenOfSpan;
            } else {
                final var spanToUseForGettingChildren = spanEventTuple.getSecondSpan();
                final var spanContainerChildrenOfSpan = getSpanContainerChildrenOfSpan(spanToUseForGettingChildren,
                        spanContainerNodes);
                return spanContainerChildrenOfSpan;
            }
        }
        // TODO throw exception
        throw new RuntimeException("SpanContainer does not hold a span or a span event tuple");
    }

    public static List<SpanContainer> getSpanContainerChildrenOfSpan(Span spanContainerSpan,
                                                               Set<SpanContainer> spanContainerSet) {
        return spanContainerSet.stream().filter(spanContainer -> {
            final var isSpanContainerSpan = spanContainer.getSpanComponent() instanceof Span;
            if (isSpanContainerSpan) {
                final var span = (Span) spanContainer.getSpanComponent();
                final var spanContainerSpanHasRefOnSpan = span.getReferences().stream()
                        .anyMatch(reference -> reference.getSpanID().equals(spanContainerSpan.getSpanID()));
                return spanContainerSpanHasRefOnSpan;
            }
            final var isSpanContainerSpanEventTuple = spanContainer.getSpanComponent() instanceof SpanEventTuple;
            if (isSpanContainerSpanEventTuple) {
                final var spanEventTuple = (SpanEventTuple) spanContainer.getSpanComponent();
                final var spanEventTupleFirstSpan = spanEventTuple.getFirstSpan();
                final var spanContainerSpanHasRefOnSpanEventTupleFirstSpan = spanEventTupleFirstSpan.getReferences()
                        .stream()
                        .anyMatch(reference -> reference.getSpanID().equals(spanContainerSpan.getSpanID()));
                return spanContainerSpanHasRefOnSpanEventTupleFirstSpan;
            }
            // TODO throw exception
            throw new RuntimeException("SpanContainer does not hold a span or a span event tuple");
        }).toList();
    }

    public static boolean hasSpanContainerSuccessorsBelongingToDifferentProcesses(SpanContainer spanContainer,
                                                                                  ImmutableGraph<SpanContainer> spanContainerGraph) {
        final var spanContainerSuccessors = spanContainerGraph.successors(spanContainer);
        final var spanContainerSuccessorsSize = spanContainerSuccessors.size();
        if (spanContainerSuccessorsSize < 2) {
            return false;
        }
        final var processesOfSuccessors = spanContainerSuccessors.stream()
                .map(each -> SpanContainerUtils.getProcessID(each))
                .distinct()
                .toList();
        return processesOfSuccessors.size() > 1;
    }
}

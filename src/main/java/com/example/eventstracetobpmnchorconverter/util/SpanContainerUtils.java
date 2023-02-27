package com.example.eventstracetobpmnchorconverter.util;

import com.example.eventstracetobpmnchorconverter.jaegerTrace.spans.Span;
import com.example.eventstracetobpmnchorconverter.producing.graph.SpanContainer;
import com.example.eventstracetobpmnchorconverter.producing.graph.SpanEventTuple;

import java.util.List;

public class SpanContainerUtils {

    public static SpanContainer createSpanContainer(final Span span, final List<Span> spanList) {
        final var spanIsNotAnEventMarker = !SpanUtils.isSpanAnEventMarker(span);
        if (spanIsNotAnEventMarker) {
            return new SpanContainer(RandomIDGenerator.generateWithPrefix("SpanContainer"), span);
        }
        final var spanIsAnEventProducerMarker = SpanUtils.isSpanAnEventProducerMarker(span);
        if (spanIsAnEventProducerMarker) {
            final var optionalChildSpanMessagingMarker = SpanUtils.getChildMessagingMarkerSpan(span, spanList);
            if (optionalChildSpanMessagingMarker.isPresent()) {
                final var spanEventTuple = new SpanEventTuple(span, optionalChildSpanMessagingMarker.get());
                return new SpanContainer(RandomIDGenerator.generateWithPrefix("SpanContainer"), spanEventTuple);
            } else {
                throw new RuntimeException("Span is an event producer marker but has no child span with messaging" +
                        ".system or messaging.destination tags");
            }
        }
        final var spanIsAnEventProcessorMarker = SpanUtils.isSpanAnEventProcessorMarker(span);
        if (spanIsAnEventProcessorMarker) {
            final var optionalParentSpanMessagingMarker = SpanUtils.getParentMessagingMarkerSpan(span, spanList);
            if (optionalParentSpanMessagingMarker.isPresent()) {
                final var spanEventTuple = new SpanEventTuple(optionalParentSpanMessagingMarker.get(), span);
                return new SpanContainer(RandomIDGenerator.generateWithPrefix("SpanContainer"), spanEventTuple);
            } else {
                throw new RuntimeException("Span is an event processor marker but has no parent span with messaging" +
                        ".system or messaging.destination tags");
            }
        }
        throw new RuntimeException("Span is an event marker but is neither an event producer nor an event processor");
    }

    public static String getProcessID(final SpanContainer spanContainer) {
        final var spanComponent = spanContainer.getSpanComponent();
        if (spanComponent instanceof Span) {
            return ((Span) spanContainer.getSpanComponent()).getProcessID();
        }
        if (spanComponent instanceof SpanEventTuple) {
            return ((SpanEventTuple) spanComponent).getFirstSpan().getProcessID();
        }
        throw new RuntimeException("SpanContainer does not contain a Span or a SpanEventTuple");
    }

}

package com.example.eventstracetobpmnchorconverter.util;

import com.example.eventstracetobpmnchorconverter.jaegerTrace.spans.Span;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SpanUtils {

    public static Optional<Span> getChildMessagingMarkerSpan(Span span, List<Span> spanList) {
        final var childSpanList = getSpanChildrenOfSpan(span, spanList);
        final var childSpanListHasMessagingMarker = childSpanList.stream()
                .anyMatch(SpanUtils::isSpanAnMessagingMarker);
        final var childSpanWithMessagingMarker = childSpanListHasMessagingMarker ? childSpanList.get(0) : null;
        return Optional.ofNullable(childSpanWithMessagingMarker);
    }

    public static Optional<Span> getParentMessagingMarkerSpan(Span span, List<Span> spanList) {
        final var parentSpanList = getSpanParentsOfSpan(span, spanList);
        final var parentSpanListHasMessagingMarker = parentSpanList.stream()
                .anyMatch(SpanUtils::isSpanAnMessagingMarker);
        final var parentSpanWithMessagingMarker = parentSpanListHasMessagingMarker ? parentSpanList.get(0) : null;
        return Optional.ofNullable(parentSpanWithMessagingMarker);
    }

    public static List<Span> getSpanChildrenOfSpan(final Span span, final List<Span> spanList) {
        return spanList.stream()
                .filter(childSpan -> childSpan.getReferences().stream()
                        .anyMatch(reference -> reference.getSpanID().equals(span.getSpanID())))
                .toList();
    }

    public static List<Span> getSpanParentsOfSpan(final Span span, final List<Span> spanList) {
        return spanList.stream()
                .filter(parentSpan -> span.getReferences().stream()
                        .anyMatch(reference -> reference.getSpanID().equals(parentSpan.getSpanID())))
                .toList();
    }


    public static boolean isSpanAnMessagingMarker(final Span span) {
        return Arrays.stream(span.getTags()).anyMatch(tag -> tag.getKey().equals("messaging" +
                ".system") || tag.getKey().equals("messaging.destination"));
    }

    public static boolean isSpanAnEventMarker(final Span span) {
        return isSpanAnEventProducerMarker(span) || isSpanAnEventProcessorMarker(span);
    }

    public static boolean isSpanAnEventProducerMarker(final Span span) {
        return Arrays.stream(span.getTags()).anyMatch(tag -> tag.getKey().equals("produced.event"));
    }

    public static boolean isSpanAnEventProcessorMarker(final Span span) {
        return Arrays.stream(span.getTags()).anyMatch(tag -> tag.getKey().equals("processed.event"));
    }
}

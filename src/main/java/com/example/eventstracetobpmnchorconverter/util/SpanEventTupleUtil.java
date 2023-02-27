package com.example.eventstracetobpmnchorconverter.util;

import com.example.eventstracetobpmnchorconverter.producing.graph.SpanEventTuple;

import java.util.Arrays;

public class SpanEventTupleUtil {

    public static boolean containsSpanEventTupleProducerMarkerSpan(final SpanEventTuple spanEventTuple) {
        final var spanEventTupleProcessorMarkerSpan = spanEventTuple.getFirstSpan();
        return Arrays.stream(spanEventTupleProcessorMarkerSpan.getTags())
                .anyMatch(tag -> tag.getKey().equals("produced.event"));
    }

    public static boolean containsSpanEventTupleProcessorMarkerSpan(final SpanEventTuple spanEventTuple) {
        final var spanEventTupleProcessorMarkerSpan = spanEventTuple.getSecondSpan();
        return Arrays.stream(spanEventTupleProcessorMarkerSpan.getTags())
                .anyMatch(tag -> tag.getKey().equals("processed.event"));
    }

}

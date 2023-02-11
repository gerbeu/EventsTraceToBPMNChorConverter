package com.example.eventstracetobpmnchorconverter.visitors.jaeger_trace;

import com.example.eventstracetobpmnchorconverter.jaegerTrace.Trace;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

@Getter
public class TopicsInfoVisitor implements TraceVisitor<Set<String>> {


    @Override
    public Set<String> visit(Trace trace) {
        final var setOfTopicsInTrace = new HashSet<String>();
        trace.getSpans().forEach(span -> {
            final var tags = span.getTags();
            final var destinationTag = Stream.of(tags)
                    .filter(tag -> tag.getKey().equals("messaging.destination"))
                    .findFirst();
            final var destinationKindTag = Stream.of(tags)
                    .filter(tag -> tag.getKey().equals("messaging.destination_kind"))
                    .findFirst();
            if (destinationTag.isPresent() && destinationKindTag.isPresent()) {
                final var messagingDestination = destinationTag.get().getValue();
                final var messagingDestinationKind = destinationKindTag.get().getValue();
                setOfTopicsInTrace.add(messagingDestination + " " + messagingDestinationKind);
            }
        });
        return setOfTopicsInTrace;
    }
}

package com.example.eventstracetobpmnchorconverter.consume.jaegerTrace.visitors;

import com.example.eventstracetobpmnchorconverter.consume.jaegerTrace.Trace;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

@Getter
public class TopicsInfoTraceVisitor implements TraceVisitor {

    private final Set<String> setOfTopicsInTrace;

    public TopicsInfoTraceVisitor() {
        this.setOfTopicsInTrace = new HashSet<>();
    }

    @Override
    public void visit(Trace trace) {
        detectTopicsInTrace(trace);
    }

    private void detectTopicsInTrace(Trace trace) {
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
    }

    public void printTopicsInfo() {
        System.out.println("Topics in trace:");
        setOfTopicsInTrace.forEach(System.out::println);
    }
}

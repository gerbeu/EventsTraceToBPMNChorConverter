package com.example.eventstracetobpmnchorconverter.visitors.jaeger_trace;

import com.example.eventstracetobpmnchorconverter.jaegerTrace.Trace;
import com.example.eventstracetobpmnchorconverter.jaegerTrace.spans.Span;
import com.example.eventstracetobpmnchorconverter.jaegerTrace.spans.Tag;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
@Getter
public class EventsInfoVisitor implements TraceVisitor<List<String>> {

    @Override
    public List<String> visit(final Trace trace) {
        return trace.getSpans().stream()
                .map(Span::getTags)
                .flatMap(Arrays::stream)
                .filter(tag -> tag.getKey().equals("produced.event"))
                .map(Tag::getValue)
                .toList();
    }
}

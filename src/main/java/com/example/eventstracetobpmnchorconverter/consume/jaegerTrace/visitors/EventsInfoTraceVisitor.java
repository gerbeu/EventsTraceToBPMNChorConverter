package com.example.eventstracetobpmnchorconverter.consume.jaegerTrace.visitors;

import com.example.eventstracetobpmnchorconverter.consume.jaegerTrace.Trace;
import com.example.eventstracetobpmnchorconverter.consume.jaegerTrace.spans.Span;
import com.example.eventstracetobpmnchorconverter.consume.jaegerTrace.spans.Tag;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
@Getter
public class EventsInfoTraceVisitor implements TraceVisitor {


    private List<String> listOfDetectedEventsInTrace;

    private void detectEventsInTrace(final Trace trace) {
        listOfDetectedEventsInTrace = trace.getSpans().stream()
                .map(Span::getTags)
                .flatMap(Arrays::stream)
                .filter(tag -> tag.getKey().equals("produced.event"))
                .map(Tag::getValue)
                .toList();
    }

    @Override
    public void visit(final Trace trace) {
        detectEventsInTrace(trace);
    }

}

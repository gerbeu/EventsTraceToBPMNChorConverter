package com.example.eventstracetobpmnchorconverter.producing.graph;

import com.example.eventstracetobpmnchorconverter.jaegerTrace.spans.Span;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SpanEventTuple extends SpanComponent {

    private final Span firstSpan;
    private final Span secondSpan;

    public SpanEventTuple(Span firstSpan, Span secondSpan) {
        this.firstSpan = firstSpan;
        this.secondSpan = secondSpan;
    }
}

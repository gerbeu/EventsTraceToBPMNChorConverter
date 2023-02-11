package com.example.eventstracetobpmnchorconverter.jaegerTrace;

import com.example.eventstracetobpmnchorconverter.jaegerTrace.processes.Process;
import com.example.eventstracetobpmnchorconverter.jaegerTrace.spans.Span;
import com.example.eventstracetobpmnchorconverter.visitors.jaeger_trace.TraceVisitor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@ToString
@Getter
@Setter
public class Trace implements TraceElement {

    private String traceID;

    private String traceName;

    private List<Span> spans;

    private Map<String, Process> processes;

    private long duration;

    private long startTime;

    private long endTime;


    @Override
    public Object accept(final TraceVisitor visitor) {
        return visitor.visit(this);
    }
}

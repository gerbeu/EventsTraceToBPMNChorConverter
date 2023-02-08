package com.example.eventstracetobpmnchorconverter.consume.jaegerTrace.visitors;

import com.example.eventstracetobpmnchorconverter.consume.jaegerTrace.Trace;
import lombok.Getter;

import java.util.HashMap;

@Getter
public class TraceProcessesInfoVisitor implements TraceVisitor {

    private final HashMap<String, String> processServiceNameHashMap;

    public TraceProcessesInfoVisitor() {
        processServiceNameHashMap = new HashMap<>();
    }

    @Override
    public void visit(final Trace trace) {
        detectProcessesInTrace(trace);
    }

    private void detectProcessesInTrace(final Trace trace) {
        trace.getProcesses().forEach((key, value) -> processServiceNameHashMap.put(key, value.getServiceName()));
    }

}

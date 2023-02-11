package com.example.eventstracetobpmnchorconverter.visitors.jaeger_trace;

import com.example.eventstracetobpmnchorconverter.jaegerTrace.Trace;
import lombok.Getter;

import java.util.HashMap;

@Getter
public class ProcessesInfoVisitor implements TraceVisitor<HashMap<String, String>> {

    @Override
    public HashMap<String, String> visit(final Trace trace) {
        final HashMap<String, String> processServiceNameHashMap = new HashMap<>();
        trace.getProcesses().forEach((key, value) -> processServiceNameHashMap.put(key, value.getServiceName()));
        return processServiceNameHashMap;
    }

}

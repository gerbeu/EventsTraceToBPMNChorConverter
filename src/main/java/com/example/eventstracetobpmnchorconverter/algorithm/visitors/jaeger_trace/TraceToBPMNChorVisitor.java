package com.example.eventstracetobpmnchorconverter.algorithm.visitors.jaeger_trace;

import com.example.eventstracetobpmnchorconverter.jaegerTrace.Trace;

public class TraceToBPMNChorVisitor implements TraceVisitor<Void>{
    @Override
    public Void visit(Trace trace) {
        return null;
    }
}

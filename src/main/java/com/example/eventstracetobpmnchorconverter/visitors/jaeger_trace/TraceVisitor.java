package com.example.eventstracetobpmnchorconverter.visitors.jaeger_trace;


import com.example.eventstracetobpmnchorconverter.jaegerTrace.Trace;

public interface TraceVisitor<T> {

    T visit(final Trace trace);

}

package com.example.eventstracetobpmnchorconverter.algorithm.visitors.jaeger_trace;


import com.example.eventstracetobpmnchorconverter.jaegerTrace.Trace;

public interface TraceVisitor<T> {

    T visit(final Trace trace);

}

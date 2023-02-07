package com.example.eventstracetobpmnchorconverter.consume.jaegerTrace.visitors;


import com.example.eventstracetobpmnchorconverter.consume.jaegerTrace.Trace;

public interface TraceVisitor {

    void visit(final Trace trace);
}

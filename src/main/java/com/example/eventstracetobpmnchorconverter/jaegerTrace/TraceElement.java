package com.example.eventstracetobpmnchorconverter.jaegerTrace;

import com.example.eventstracetobpmnchorconverter.algorithm.visitors.jaeger_trace.TraceVisitor;

public interface TraceElement {

    Object accept(final TraceVisitor visitor);

}

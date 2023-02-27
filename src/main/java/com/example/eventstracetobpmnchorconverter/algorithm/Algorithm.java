package com.example.eventstracetobpmnchorconverter.algorithm;

import com.example.eventstracetobpmnchorconverter.jaegerTrace.Trace;

public interface Algorithm <T> {
    T run(Trace trace);
}

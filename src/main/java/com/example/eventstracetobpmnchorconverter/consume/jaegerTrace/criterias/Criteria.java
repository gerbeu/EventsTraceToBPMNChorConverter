package com.example.eventstracetobpmnchorconverter.consume.jaegerTrace.criterias;

import com.example.eventstracetobpmnchorconverter.consume.jaegerTrace.Trace;

public interface Criteria {

    public void meetCriteria(final Trace trace);

}
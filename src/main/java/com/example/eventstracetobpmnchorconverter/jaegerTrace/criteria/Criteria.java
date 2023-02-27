package com.example.eventstracetobpmnchorconverter.jaegerTrace.criteria;

import com.example.eventstracetobpmnchorconverter.jaegerTrace.Trace;

public interface Criteria {

    public void meetCriteria(final Trace trace);

}
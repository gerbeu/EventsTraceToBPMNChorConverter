package com.example.eventstracetobpmnchorconverter.consume.jaegerTrace.spans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;

@AllArgsConstructor
@ToString
@Getter
@Setter
public class Span {

    private String traceID;

    private String spanID;

    private String operationName;

    private long startTime;

    private String processID;

    private Tag[] tags;

    private ArrayList<Reference> references;

    private boolean hasChildren;

}

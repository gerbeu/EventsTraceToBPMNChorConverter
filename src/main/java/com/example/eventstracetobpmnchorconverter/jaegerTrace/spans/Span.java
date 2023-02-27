package com.example.eventstracetobpmnchorconverter.jaegerTrace.spans;

import com.example.eventstracetobpmnchorconverter.producing.graph.SpanComponent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@AllArgsConstructor
@Getter
@Setter
public class Span extends SpanComponent {

    private String traceID;

    private String spanID;

    private String operationName;

    private long startTime;

    private String processID;

    private Tag[] tags;

    private ArrayList<Reference> references;

    private boolean hasChildren;

    @Override
    public String toString() {
        return "Span{" +
                "spanID='" + spanID + '\'' +
                ", operationName='" + operationName + '\'' +
                '}';
    }
}

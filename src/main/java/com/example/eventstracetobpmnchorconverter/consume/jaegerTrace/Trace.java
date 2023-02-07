package com.example.eventstracetobpmnchorconverter.consume.jaegerTrace;

import com.example.eventstracetobpmnchorconverter.consume.jaegerTrace.processes.Process;
import com.example.eventstracetobpmnchorconverter.consume.jaegerTrace.spans.Span;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Map;

@AllArgsConstructor
@ToString
@Getter
@Setter
public class Trace {

    private String traceID;

    private String traceName;

    private ArrayList<Span> spans;

    private Map<String, Process> processes;

    private long duration;

    private long startTime;

    private long endTime;

}

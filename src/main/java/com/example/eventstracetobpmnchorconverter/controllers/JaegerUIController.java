package com.example.eventstracetobpmnchorconverter.controllers;

import com.example.eventstracetobpmnchorconverter.algorithm.EventDrivenBPMNChoreographyAlgorithm;
import com.example.eventstracetobpmnchorconverter.algorithm.result.EventDrivenBPMNChoreographyResult;
import com.example.eventstracetobpmnchorconverter.jaegerTrace.Trace;
import com.example.eventstracetobpmnchorconverter.jaegerTrace.criteria.ProcessTagCriteria;
import com.example.eventstracetobpmnchorconverter.jaegerTrace.criteria.TagCriteria;
import com.example.eventstracetobpmnchorconverter.algorithm.visitors.jaeger_trace.TopicsInfoVisitor;
import com.example.eventstracetobpmnchorconverter.algorithm.visitors.jaeger_trace.TraceToSpanGuavaGraphVisitor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;

import java.text.MessageFormat;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
public class JaegerUIController {

    @Autowired
    private final EventDrivenBPMNChoreographyAlgorithm eventDrivenBPMNChoreographyAlgorithm;

    public JaegerUIController(EventDrivenBPMNChoreographyAlgorithm eventDrivenBPMNChoreographyAlgorithm) {
        this.eventDrivenBPMNChoreographyAlgorithm = eventDrivenBPMNChoreographyAlgorithm;
    }

    private void printSpanNamesOfTrace(final Trace trace) {
        trace.getSpans().forEach(
                span -> log.info("Span name: " + span.getOperationName())
        );
    }

    private void printSpansInformationOfTrace(final Trace trace) {
        trace.getSpans().forEach(
                span -> {
                    final var spanInfo = MessageFormat.format("Span name: {0}, Span id: {1}",
                            span.getOperationName(), span.getSpanID());
                }
        );
    }

    private void printTraceAsJson(final Trace trace) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            final var jsonString = objectMapper.writeValueAsString(trace);
            // Pretty print json in log info
            log.info(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(trace));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("trace-to-java")
    public void processTraceToJava(@RequestBody Trace trace) {
        log.info("In processTraceToJava");
        TagCriteria tagCriteria = new TagCriteria();
        tagCriteria.meetCriteria(trace);
        log.info("Performing processTagCriteria filtering");
        ProcessTagCriteria processTagCriteria = new ProcessTagCriteria();
        processTagCriteria.meetCriteria(trace);
        log.info(trace.toString());
        printSpanNamesOfTrace(trace);
        printTraceAsJson(trace);
    }

    @PostMapping("/print-body")
    public void printRequestBody(@RequestBody Trace trace) {
        log.info("In printRequestBody");
        log.info(trace.toString());
    }


    @PostMapping("/get-events-from-trace")
    public void processTrace(@RequestBody Trace trace) {
        log.info("In processTrace");
        log.info(trace.toString());
    }


    @PostMapping("/get-topics-from-trace")
    public void processTopicsFromTrace(@RequestBody Trace trace) {
        log.info("In processTopicsFromTrace");
        final var topicsInfoTraceVisitor = new TopicsInfoVisitor();
        final var topicsInfo = (List<String>) trace.accept(topicsInfoTraceVisitor);
        topicsInfo.forEach(log::info);
    }

    @PostMapping("/transform-trace-to-bpmn-choreography")
    public void processTraceToBPMNChor(@RequestBody Trace trace) {
        log.info("In processTraceToBPMNChor");
        eventDrivenBPMNChoreographyAlgorithm.convertTraceToBPMNChorResponse(trace);
    }

    @PostMapping("/transform-trace-to-guava-graph")
    public void processTraceToGuavaGraph(@RequestBody Trace trace) {
        log.info("In processTraceToBPMNChor");
        final var traceToSpanGuavaGraphVisitor = new TraceToSpanGuavaGraphVisitor();
        trace.accept(traceToSpanGuavaGraphVisitor);

    }

    @PostMapping("/algorithm")
    @ResponseBody()
    public ResponseEntity<EventDrivenBPMNChoreographyResult> processAlgorithmRequest(@RequestBody Trace trace) {
        log.info("In processAlgorithmRequest");
        final var result = eventDrivenBPMNChoreographyAlgorithm.run(trace);
        return ResponseEntity.ok(result);
    }

}
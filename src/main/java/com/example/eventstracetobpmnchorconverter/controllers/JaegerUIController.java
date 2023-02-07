package com.example.eventstracetobpmnchorconverter.controllers;

import com.example.eventstracetobpmnchorconverter.consume.jaegerTrace.Trace;
import com.example.eventstracetobpmnchorconverter.consume.jaegerTrace.criterias.ProcessTagCriteria;
import com.example.eventstracetobpmnchorconverter.consume.jaegerTrace.criterias.TagCriteria;
import com.example.eventstracetobpmnchorconverter.consume.jaegerTrace.graph_data_structure.SpanGraphCreator;
import com.example.eventstracetobpmnchorconverter.consume.jaegerTrace.visitors.TopicsInfoTraceVisitor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
public class JaegerUIController {

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

    private void createSpanGraph(final Trace trace) {
        final var spanGraphCreator = new SpanGraphCreator();
        final var spanGraph = spanGraphCreator.createSpanGraph(trace.getSpans());
        spanGraph.print();

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
        createSpanGraph(trace);
    }


    @PostMapping("/get-events-from-trace")
    public void processTrace(@RequestBody Trace trace) {
        log.info("In processTrace");
        log.info(trace.toString());
    }


    @PostMapping("/get-topics-from-trace")
    public void processTopicsFromTrace(@RequestBody Trace trace) {
        log.info("In processTopicsFromTrace");
        final var topicsInfoTraceVisitor = new TopicsInfoTraceVisitor();
        topicsInfoTraceVisitor.visit(trace);
        topicsInfoTraceVisitor.printTopicsInfo();
    }

    @PostMapping("/transform-trace-to-bpmn-choreography")
    public void processTraceToBPMNChor(@RequestBody Trace trace) {
        log.info("In processTraceToBPMNChor");
    }

}
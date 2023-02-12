package com.example.eventstracetobpmnchorconverter.algorithm;

import com.example.eventstracetobpmnchorconverter.algorithm.converters.ChoreographyGraphToBPMNDiagramConverter;
import com.example.eventstracetobpmnchorconverter.algorithm.result.EventDrivenBPMNChoreographyResult;
import com.example.eventstracetobpmnchorconverter.algorithm.visitors.jaeger_trace.EventsInfoVisitor;
import com.example.eventstracetobpmnchorconverter.algorithm.visitors.jaeger_trace.ProcessesInfoVisitor;
import com.example.eventstracetobpmnchorconverter.algorithm.visitors.jaeger_trace.TopicsInfoVisitor;
import com.example.eventstracetobpmnchorconverter.algorithm.visitors.jaeger_trace.TraceToSpanContainerGraphVisitor;
import com.example.eventstracetobpmnchorconverter.producing.information.EventsInfo;
import com.example.eventstracetobpmnchorconverter.producing.information.TopicsInfo;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.BPMNDefinitions;
import com.example.eventstracetobpmnchorconverter.jaegerTrace.Trace;
import com.example.eventstracetobpmnchorconverter.jaegerTrace.criteria.ProcessTagCriteria;
import com.example.eventstracetobpmnchorconverter.jaegerTrace.criteria.TagCriteria;
import com.example.eventstracetobpmnchorconverter.algorithm.converters.SpanContainerGraphToChoreographyGraphConverter;
import com.example.eventstracetobpmnchorconverter.producing.graph.SpanContainer;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.Message;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.bpmndiagram.BPMNDiagram;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.Choreography;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.ChoreographyShape;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.MessageFlow;
import com.example.eventstracetobpmnchorconverter.util.RandomIDGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.graph.ImmutableGraph;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class EventDrivenBPMNChoreographyAlgorithm implements Algorithm<EventDrivenBPMNChoreographyResult> {

    private final Trace trace;

    private final EventsInfoVisitor eventsInfoVisitor = new EventsInfoVisitor();

    private final TopicsInfoVisitor topicsInfoVisitor = new TopicsInfoVisitor();

    private final ProcessesInfoVisitor processesInfoVisitor = new ProcessesInfoVisitor();

    private EventsInfo eventsInfo;

    private TopicsInfo topicsInfo;

    private BPMNDefinitions bpmnDefinitions;

    private Choreography choreography;

    private BPMNDiagram bpmnDiagram;

    private ImmutableGraph<SpanContainer> spanContainerGraph;

    private ImmutableGraph<ChoreographyShape> choreographyGraph;

    private List<Message> messageHashSet;

    private Map<Message, MessageFlow> messageMessageFlowMap;

    public EventDrivenBPMNChoreographyAlgorithm(Trace trace) {
        this.trace = trace;
    }

    @Override
    public EventDrivenBPMNChoreographyResult run() {
        filterTrace();
        createEventsInfo();
        createTopicsInfo();
        createSpanContainerGraphFromTrace();
        createChoreographyGraphAndChoreography();
        createBPMNChoreographyDiagram();
        log.info("Algorithm finished");
        printChoreography();
        bpmnDefinitions = new BPMNDefinitions(RandomIDGenerator.generate(), new ArrayList<>(messageHashSet), choreography, bpmnDiagram);
        printBPMNDefintions();
        XmlMapper xmlMapper = new XmlMapper();
        String xml = null;
        try {
            xml = xmlMapper.writeValueAsString(bpmnDefinitions);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return new EventDrivenBPMNChoreographyResult(null, null, xml);
    }

    private void printBPMNDefintions() {
        log.info("BPMNDefinitions: {}", bpmnDefinitions);
        XmlMapper xmlMapper = new XmlMapper();
        String xml = null;
        try {
            xml = xmlMapper.writeValueAsString(bpmnDefinitions);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println(xml);
    }

    //TODO REMOVE
    private void printChoreography() {
        log.info("Choreography: {}", choreography);
        XmlMapper xmlMapper = new XmlMapper();
        String xml = null;
        try {
            xml = xmlMapper.writeValueAsString(choreography);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println(xml);
    }

    private void filterTrace() {
        final var tagCriteria = new TagCriteria();
        tagCriteria.meetCriteria(this.trace);
        final var processTagCriteria = new ProcessTagCriteria();
        processTagCriteria.meetCriteria(this.trace);
    }

    public void convertTraceToBPMNChorResponse(final Trace trace) {
        // Perform filtering of trace with all specified criteria
        final var tagCriteria = new TagCriteria();
        tagCriteria.meetCriteria(trace);
        final var processTagCriteria = new ProcessTagCriteria();
        processTagCriteria.meetCriteria(trace);
        // Detect all occurred events in the trace
        trace.accept(eventsInfoVisitor);
        final var listOfDetectedEventsInTrace = (List<String>) trace.accept(eventsInfoVisitor);
        // Detect all occured topics in the trace
        trace.accept(topicsInfoVisitor);
        final var setOfDetectedTopicsInTrace = (Set<String>) trace.accept(topicsInfoVisitor);
        // Detect all processes (microservices) in the trace
        trace.accept(processesInfoVisitor);
        final var mapOfDetectedProcessesInTrace = (Map<String, String>) trace.accept(processesInfoVisitor);
        // TODO remove Print all spans
        //continueV2(trace, mapOfDetectedProcessesInTrace, setOfDetectedTopicsInTrace);
        continueV3(trace, mapOfDetectedProcessesInTrace);
    }

    private void createSpanContainerGraphFromTrace() {
        log.info("Creating SpanContainerGraph from Trace");
        final var traceToSpanContainerGraphVisitor = new TraceToSpanContainerGraphVisitor();
        this.spanContainerGraph = (ImmutableGraph<SpanContainer>) this.trace.accept(traceToSpanContainerGraphVisitor);
    }

    private void createChoreographyGraphAndChoreography() {
        log.info("Creating ChoreographyGraph from SpanContainerGraph");
        trace.accept(processesInfoVisitor);
        final var mapOfDetectedProcessesInTrace = (Map<String, String>) this.trace.accept(processesInfoVisitor);
        final var spanContainerGraphToChoreographyGraphConverter = new SpanContainerGraphToChoreographyGraphConverter(
                spanContainerGraph, mapOfDetectedProcessesInTrace);
        this.choreographyGraph = spanContainerGraphToChoreographyGraphConverter.convert();
        this.messageHashSet = spanContainerGraphToChoreographyGraphConverter.getMessages();
        this.messageMessageFlowMap = spanContainerGraphToChoreographyGraphConverter.getMessageMessageFlowMap();
        this.choreography = spanContainerGraphToChoreographyGraphConverter.createChoreography();
    }

    private void createBPMNChoreographyDiagram() {
        log.info("Creating BPMN Choreography Diagram from ChoreographyGraph and Choreography");
        final var choreographyGraphToBPMNDiagramConverter =
                new ChoreographyGraphToBPMNDiagramConverter(choreographyGraph, choreography.getId(), messageHashSet,
                        messageMessageFlowMap);
        this.bpmnDiagram = choreographyGraphToBPMNDiagramConverter.convert();
        XmlMapper xmlMapper = new XmlMapper();
        String xml = null;
        try {
            xml = xmlMapper.writeValueAsString(bpmnDiagram);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println(xml);

    }

    private void createTopicsInfo() {
        log.info("Creating TopicsInfo from Trace");

    }

    private void createEventsInfo() {
        log.info("Creating EventsInfo from Trace");

    }


    private void continueV3(final Trace trace, final Map<String, String> mapOfDetectedProcessesInTrace) {
        TraceToSpanContainerGraphVisitor traceToSpanContainerGraphVisitor = new TraceToSpanContainerGraphVisitor();
        final var spanContainerGraph = (ImmutableGraph<SpanContainer>) trace.accept(traceToSpanContainerGraphVisitor);
        System.out.println("trace: " + trace);
        final var spanContainerGraphToChoreographyGraphConverter =
                new SpanContainerGraphToChoreographyGraphConverter(spanContainerGraph, mapOfDetectedProcessesInTrace);
        spanContainerGraphToChoreographyGraphConverter.createChoreographyGraph();
    }
}

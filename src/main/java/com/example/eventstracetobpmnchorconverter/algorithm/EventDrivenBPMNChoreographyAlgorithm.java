package com.example.eventstracetobpmnchorconverter.algorithm;

import com.example.eventstracetobpmnchorconverter.algorithm.converters.ChoreographyGraphToBPMNDiagramConverter;
import com.example.eventstracetobpmnchorconverter.algorithm.converters.ChoreographyGraphToChoreographyConverter;
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
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.Choreography;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.ChoreographyShape;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.MessageFlow;
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

    private ImmutableGraph<SpanContainer> spanContainerGraph;

    private ImmutableGraph<ChoreographyShape> choreographyGraph;

    private Set<Message> messageHashSet;

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
        createChoreographyGraphFromSpanContainerGraph();
        createChoreography();
        createBPMNChoreographyDiagram();
        return null;
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

    private void createChoreographyGraphFromSpanContainerGraph() {
        log.info("Creating ChoreographyGraph from SpanContainerGraph");
        trace.accept(processesInfoVisitor);
        final var mapOfDetectedProcessesInTrace = (Map<String, String>) this.trace.accept(processesInfoVisitor);
        final var spanContainerGraphToChoreographyGraphConverter = new SpanContainerGraphToChoreographyGraphConverter(
                spanContainerGraph, mapOfDetectedProcessesInTrace);
        this.choreographyGraph = spanContainerGraphToChoreographyGraphConverter.convert();
        this.messageHashSet = spanContainerGraphToChoreographyGraphConverter.getMessageHashSet();
        this.messageMessageFlowMap = spanContainerGraphToChoreographyGraphConverter.getMessageMessageFlowMap();

    }

    private void createChoreography() {
        log.info("Creating Choreography from ChoreographyGraph");
        final var choreographyGraphToChoreographyConverter = new ChoreographyGraphToChoreographyConverter();
        // TODO final var choreography = choreographyGraphToChoreographyConverter.convert(choreographyGraph);
        // TODO  this.choreography = new Choreogra
        this.choreography = new Choreography("CHOR_1");
    }

    private void createBPMNChoreographyDiagram() {
        log.info("Creating BPMN Choreography Diagram from ChoreographyGraph and Choreography");
        final var choreographyGraphToBPMNDiagramConverter =
                new ChoreographyGraphToBPMNDiagramConverter(choreographyGraph, choreography.getId(), messageHashSet,
                        messageMessageFlowMap);
        final var bpmnDiagram = choreographyGraphToBPMNDiagramConverter.convert();
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

package com.example.eventstracetobpmnchorconverter.algorithm;

import com.example.eventstracetobpmnchorconverter.algorithm.converters.ChoreographyGraphToBPMNDiagramConverter;
import com.example.eventstracetobpmnchorconverter.algorithm.result.EventDrivenBPMNChoreographyResult;
import com.example.eventstracetobpmnchorconverter.algorithm.services.MicroservicesInfoService;
import com.example.eventstracetobpmnchorconverter.algorithm.services.TopicsEventsInfoService;
import com.example.eventstracetobpmnchorconverter.algorithm.visitors.jaeger_trace.TraceToSpanContainerGraphVisitor;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.*;

@Service
@Slf4j
@RequestScope
public class EventDrivenBPMNChoreographyAlgorithm implements Algorithm<EventDrivenBPMNChoreographyResult> {

    private Trace trace;

    private BPMNDefinitions bpmnDefinitions;

    private Choreography choreography;

    private BPMNDiagram bpmnDiagram;

    private ImmutableGraph<SpanContainer> spanContainerGraph;

    private ImmutableGraph<ChoreographyShape> choreographyGraph;

    private List<Message> messageHashSet;

    private Map<Message, MessageFlow> messageMessageFlowMap;

    @Autowired
    private final SpanContainerGraphToChoreographyGraphConverter spanContainerGraphToChoreographyGraphConverter;

    @Autowired
    private final TopicsEventsInfoService topicsEventsInfoService;

    @Autowired
    private final MicroservicesInfoService microservicesInfoService;

    public EventDrivenBPMNChoreographyAlgorithm(SpanContainerGraphToChoreographyGraphConverter spanContainerGraphToChoreographyGraphConverter,
                                                TopicsEventsInfoService topicsEventsInfoService, MicroservicesInfoService microservicesInfoService) {
        this.spanContainerGraphToChoreographyGraphConverter = spanContainerGraphToChoreographyGraphConverter;
        this.topicsEventsInfoService = topicsEventsInfoService;
        this.microservicesInfoService = microservicesInfoService;
    }


    @Override
    public EventDrivenBPMNChoreographyResult run(Trace trace) {
        this.trace = trace;
        filterTrace();
        createSpanContainerGraphFromTrace();
        createChoreographyGraphAndChoreography();
        createBPMNChoreographyDiagram();
        return createEventDrivenBPMNChoreographyResult();
    }

    private void filterTrace() {
        final var tagCriteria = new TagCriteria();
        tagCriteria.meetCriteria(this.trace);
        final var processTagCriteria = new ProcessTagCriteria();
        processTagCriteria.meetCriteria(this.trace);
    }

    private void createSpanContainerGraphFromTrace() {
        log.info("Creating SpanContainerGraph from Trace");
        final var traceToSpanContainerGraphVisitor = new TraceToSpanContainerGraphVisitor();
        this.spanContainerGraph = (ImmutableGraph<SpanContainer>) this.trace.accept(traceToSpanContainerGraphVisitor);
    }

    private void createChoreographyGraphAndChoreography() {
        log.info("Creating ChoreographyGraph from SpanContainerGraph");
        microservicesInfoService.createMicroservicesInfo(trace);
        final var mapOfDetectedProcessesInTrace = microservicesInfoService.getProcessMicroserviceMap();
        spanContainerGraphToChoreographyGraphConverter.init(spanContainerGraph, mapOfDetectedProcessesInTrace);
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
    }

    private EventDrivenBPMNChoreographyResult createEventDrivenBPMNChoreographyResult() {
        bpmnDefinitions = new BPMNDefinitions(RandomIDGenerator.generate(), new ArrayList<>(messageHashSet), choreography, bpmnDiagram);
        final var bpmnDefinitionsXml = createXmlStringFromBPMNDefinitions();
        final var topicsEventsInfo = topicsEventsInfoService.getTopics();
        return new EventDrivenBPMNChoreographyResult(bpmnDefinitionsXml, topicsEventsInfo);
    }

    private String createXmlStringFromBPMNDefinitions() {
        XmlMapper xmlMapper = new XmlMapper();
        String xml = null;
        try {
            xml = xmlMapper.writeValueAsString(bpmnDefinitions);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println(xml);
        return xml;
    }

}

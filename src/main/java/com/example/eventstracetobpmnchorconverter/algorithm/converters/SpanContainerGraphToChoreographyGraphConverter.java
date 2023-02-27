package com.example.eventstracetobpmnchorconverter.algorithm.converters;

import com.example.eventstracetobpmnchorconverter.layout.BPMNChoreography2DGraphLayoutCreator;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.Message;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.*;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.choreographytask.ChoreographyTask;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.events.EndEvent;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.events.StartEvent;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.gateway.Gateway;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.gateway.ParallelGateway;
import com.example.eventstracetobpmnchorconverter.producing.graph.SpanContainer;
import com.example.eventstracetobpmnchorconverter.util.RandomIDGenerator;
import com.example.eventstracetobpmnchorconverter.util.SpanContainerGraphUtils;
import com.example.eventstracetobpmnchorconverter.util.SpanContainerToChoreographyTaskUtil;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;
import com.google.common.graph.MutableGraph;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.*;

@Service
@Slf4j
@Getter
@RequestScope
public class SpanContainerGraphToChoreographyGraphConverter implements Converter<ImmutableGraph<ChoreographyShape>> {

    private final Set<Participant> participantHashSet;

    private final List<Message> messages;

    private final Map<Message, MessageFlow> messageMessageFlowMap;

    private final Map<SpanContainer, ChoreographyShape> spanContainersWithChoreographyShape;

    private final Set<SequenceFlow> sequenceFlowHashSet;

    private Map<String, String> mapOfDetectedProcessesInTrace;

    private ImmutableGraph<SpanContainer> spanContainerGraph;

    @Getter(AccessLevel.NONE)
    private final MutableGraph<ChoreographyShape> choreographyGraph;

    @Autowired
    private final SpanContainerToChoreographyTaskUtil spanContainerToChoreographyTaskUtil;


    public SpanContainerGraphToChoreographyGraphConverter(SpanContainerToChoreographyTaskUtil spanContainerToChoreographyTaskUtil) {
        this.spanContainerToChoreographyTaskUtil = spanContainerToChoreographyTaskUtil;
        this.participantHashSet = new HashSet<>();
        this.messages = new ArrayList<>();
        this.messageMessageFlowMap = new HashMap<>();
        this.spanContainersWithChoreographyShape = new HashMap<>();
        this.sequenceFlowHashSet = new HashSet<>();
        this.choreographyGraph = GraphBuilder.directed().build();
    }

    public void init(ImmutableGraph<SpanContainer> spanContainerGraph,
                     Map<String, String> mapOfDetectedProcessesInTrace) {
        this.spanContainerGraph = spanContainerGraph;
        this.mapOfDetectedProcessesInTrace = mapOfDetectedProcessesInTrace;
    }

    @Override
    public ImmutableGraph<ChoreographyShape> convert() {
        createChoreographyGraph();
        addCoordinatesToEachChoreographyShape();
        return getChoreographyGraph();
    }

    public Choreography createChoreography() {
        log.info("Creating Choreography");
        final var choreographyShapes = new ArrayList<>(choreographyGraph.nodes());
        final var choreographyTasks = choreographyShapes.stream()
                .filter(choreographyShape -> choreographyShape instanceof ChoreographyTask)
                .map(choreographyShape -> (ChoreographyTask) choreographyShape)
                .toList();
        final var parallelGateways = choreographyShapes.stream()
                .filter(choreographyShape -> choreographyShape instanceof ParallelGateway)
                .map(choreographyShape -> (ParallelGateway) choreographyShape)
                .toList();
        final var startEvent = choreographyShapes.stream()
                .filter(choreographyShape -> choreographyShape instanceof StartEvent)
                .map(choreographyShape -> (StartEvent) choreographyShape)
                .findFirst()
                .orElseThrow();
        final var endEvent = choreographyShapes.stream()
                .filter(choreographyShape -> choreographyShape instanceof EndEvent)
                .map(choreographyShape -> (EndEvent) choreographyShape)
                .findFirst()
                .orElseThrow();
        final var choreography = Choreography.builder()
                .id(RandomIDGenerator.generate())
                .name("Choreography")
                .choreographyTasks(choreographyTasks)
                .participants(new ArrayList<>(participantHashSet))
                .messageFlows(new ArrayList<>(messageMessageFlowMap.values()))
                .startEvent(startEvent)
                .endEvent(endEvent)
                .sequenceFlows(new ArrayList<>(sequenceFlowHashSet))
                .gateways(parallelGateways)
                .build();
        return choreography;
    }

    public ImmutableGraph<ChoreographyShape> getChoreographyGraph() {
        return ImmutableGraph.copyOf(choreographyGraph);
    }


    public void createChoreographyGraph() {
        log.info("Creating ChoreographyGraph from SpanContainerGraph");
        // Add StartEvent
        final var startEvent = new StartEvent(RandomIDGenerator.generateWithPrefix("StartEvent"));
        choreographyGraph.addNode(startEvent);
        for (int i = 0; i < spanContainerGraph.nodes().size(); i++) {
            final var spanContainer = List.copyOf(spanContainerGraph.nodes()).get(i);
            log.info("SpanContainer: " + spanContainer);
            final var existsChoreographyTaskForSpanContainer = spanContainersWithChoreographyShape.containsKey(spanContainer);
            ChoreographyShape choreographyShape = null;
            if (!existsChoreographyTaskForSpanContainer) {
                if (i == 0) {
                    // Creating first ChoreographyTask
                    choreographyShape = spanContainerToChoreographyTaskUtil.createFirstChoreographyTaskFromSpanContainer(
                            spanContainer, mapOfDetectedProcessesInTrace, participantHashSet, messages, messageMessageFlowMap);
                } else {
                    choreographyShape = spanContainerToChoreographyTaskUtil.createChoreographyTaskFromSpanContainer(
                            spanContainer, mapOfDetectedProcessesInTrace, participantHashSet, messages, messageMessageFlowMap);
                }
            } else {
                choreographyShape = (ChoreographyTask) spanContainersWithChoreographyShape.get(spanContainer);
            }
            spanContainersWithChoreographyShape.put(spanContainer, choreographyShape);
            final var hasSpanContainerSuccessorsBelongingToDifferentProcesses =
                    SpanContainerGraphUtils.hasSpanContainerSuccessorsBelongingToDifferentProcesses(
                            spanContainer,
                            spanContainerGraph);
            if (hasSpanContainerSuccessorsBelongingToDifferentProcesses) {
                final var choreographyTask = (ChoreographyTask) choreographyShape;
                choreographyShape = new ParallelGateway(RandomIDGenerator.generateWithPrefix("ParallelGateway"));
                final var sequenceFlow = new SequenceFlow(RandomIDGenerator.generateWithPrefix("SequenceFlow"));
                sequenceFlow.setSourceRef(choreographyTask.getId());
                sequenceFlow.setSourceRef(choreographyShape.getId());
                choreographyTask.setOutgoingFlow(sequenceFlow.getId());
                ((ParallelGateway) choreographyShape).addIncomingFlow(sequenceFlow.getId());
                sequenceFlowHashSet.add(sequenceFlow);

                choreographyGraph.putEdge(choreographyTask, choreographyShape);
            }
            createChoreographyShapeForSuccessors(
                    choreographyShape,
                    spanContainer);
        }
        final var firstChoreographyShape =
                (ChoreographyTask) spanContainersWithChoreographyShape.get(List.copyOf(spanContainerGraph.nodes()).get(0));
        final var sequenceFlow = new SequenceFlow(RandomIDGenerator.generateWithPrefix("SequenceFlow"));
        sequenceFlow.setSourceRef(startEvent.getId());
        sequenceFlow.setTargetRef(firstChoreographyShape.getId());
        startEvent.setOutgoingFlow(sequenceFlow.getId());
        firstChoreographyShape.setIncomingFlow(sequenceFlow.getId());
        sequenceFlowHashSet.add(sequenceFlow);
        choreographyGraph.putEdge(startEvent, firstChoreographyShape);
        final var lastEdge = spanContainerGraph.edges().stream()
                .reduce((first, second) -> second)
                .orElseThrow();
        final var lastChoreographyTask = (ChoreographyTask) spanContainersWithChoreographyShape.get(lastEdge.target());
        final var endEvent = new EndEvent(RandomIDGenerator.generateWithPrefix("EndEvent"));
        final var lastSequenceFlow = new SequenceFlow(RandomIDGenerator.generateWithPrefix("SequenceFlow"));
        lastSequenceFlow.setSourceRef(lastChoreographyTask.getId());
        lastSequenceFlow.setTargetRef(endEvent.getId());
        lastChoreographyTask.setOutgoingFlow(lastSequenceFlow.getId());
        endEvent.addIncomingFlow(lastSequenceFlow.getId());
        choreographyGraph.putEdge(lastChoreographyTask, endEvent);
    }

    private void createChoreographyShapeForSuccessors(final ChoreographyShape choreographyShape,
                                                      final SpanContainer spanContainer) {
        for (final var successor : spanContainerGraph.successors(spanContainer)) {
            ChoreographyTask successorChoreographyTask =
                    (ChoreographyTask) spanContainersWithChoreographyShape.get(successor);
            final var sequenceFlow = new SequenceFlow(RandomIDGenerator.generateWithPrefix("SequenceFlow"));
            if (successorChoreographyTask == null) {
                log.info("Creating ChoreographyTask for successor: " + successor);
                successorChoreographyTask = spanContainerToChoreographyTaskUtil.createChoreographyTaskFromSpanContainer(
                        successor,
                        mapOfDetectedProcessesInTrace,
                        participantHashSet,
                        messages,
                        messageMessageFlowMap);
                successorChoreographyTaskNotNull(choreographyShape,
                        successorChoreographyTask,
                        sequenceFlow);
                spanContainersWithChoreographyShape.put(successor, successorChoreographyTask);
            } else {
                successorChoreographyTaskNotNull(choreographyShape,
                        successorChoreographyTask,
                        sequenceFlow);
            }
        }
    }

    private void successorChoreographyTaskNotNull(final ChoreographyShape choreographyShape,
                                                  final ChoreographyTask successorChoreographyTask,
                                                  final SequenceFlow sequenceFlow) {
        if (choreographyShape instanceof Gateway) {
            sequenceFlow.setSourceRef(choreographyShape.getId());
            sequenceFlow.setTargetRef(successorChoreographyTask.getId());
            ((ParallelGateway) choreographyShape).addOutgoingFlow(sequenceFlow.getId());
            successorChoreographyTask.setIncomingFlow(sequenceFlow.getId());
            sequenceFlowHashSet.add(sequenceFlow);
        } else if (choreographyShape instanceof ChoreographyTask) {
            sequenceFlow.setSourceRef(choreographyShape.getId());
            sequenceFlow.setTargetRef(successorChoreographyTask.getId());
            ((ChoreographyTask) choreographyShape).setOutgoingFlow(sequenceFlow.getId());
            successorChoreographyTask.setIncomingFlow(sequenceFlow.getId());
            sequenceFlowHashSet.add(sequenceFlow);
        } else {
            throw new RuntimeException("ChoreographyShape is not a ChoreographyTask or a Gateway");
        }
        choreographyGraph.putEdge(choreographyShape, successorChoreographyTask);
    }

    private void addCoordinatesToEachChoreographyShape() {
        final var rootChoreographyShape = this.choreographyGraph.nodes().iterator().next();
        final var bpmnChoreography2DGraphLayoutCreator = new BPMNChoreography2DGraphLayoutCreator();
        bpmnChoreography2DGraphLayoutCreator.layout(this.choreographyGraph, rootChoreographyShape);
    }
}

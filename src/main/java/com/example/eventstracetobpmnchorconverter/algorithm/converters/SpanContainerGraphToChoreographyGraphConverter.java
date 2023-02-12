package com.example.eventstracetobpmnchorconverter.algorithm.converters;

import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.Message;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.ChoreographyShape;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.MessageFlow;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.Participant;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.SequenceFlow;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.choreographytask.ChoreographyTask;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.events.StartEvent;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.gateway.Gateway;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.gateway.ParallelGateway;
import com.example.eventstracetobpmnchorconverter.producing.graph.SpanContainer;
import com.example.eventstracetobpmnchorconverter.util.SpanContainerGraphUtils;
import com.example.eventstracetobpmnchorconverter.util.SpanContainerToChoreographyTaskUtil;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;
import com.google.common.graph.MutableGraph;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@Getter
public class SpanContainerGraphToChoreographyGraphConverter implements Converter<ImmutableGraph<ChoreographyShape>> {

    private final Set<Participant> participantHashSet;

    private final Set<Message> messageHashSet;

    private final Map<Message, MessageFlow> messageMessageFlowMap;

    private final Map<SpanContainer, ChoreographyShape> spanContainersWithChoreographyShape;

    private final Set<SequenceFlow> sequenceFlowHashSet;

    private final Map<String, String> mapOfDetectedProcessesInTrace;

    private final ImmutableGraph<SpanContainer> spanContainerGraph;

    @Getter(AccessLevel.NONE)
    private final MutableGraph<ChoreographyShape> choreographyGraph;

    public SpanContainerGraphToChoreographyGraphConverter(ImmutableGraph<SpanContainer> spanContainerGraph,
                                                          Map<String, String> mapOfDetectedProcessesInTrace) {
        this.spanContainerGraph = spanContainerGraph;
        this.mapOfDetectedProcessesInTrace = mapOfDetectedProcessesInTrace;
        this.participantHashSet = new HashSet<>();
        this.messageHashSet = new HashSet<>();
        this.messageMessageFlowMap = new HashMap<>();
        this.spanContainersWithChoreographyShape = new HashMap<>();
        this.sequenceFlowHashSet = new HashSet<>();
        this.choreographyGraph = GraphBuilder.directed().build();
    }

    @Override
    public ImmutableGraph<ChoreographyShape> convert() {
        createChoreographyGraph();
        return getChoreographyGraph();
    }

    public ImmutableGraph<ChoreographyShape> getChoreographyGraph() {
        return ImmutableGraph.copyOf(choreographyGraph);
    }


    public void createChoreographyGraph() {
        log.info("Creating ChoreographyGraph from SpanContainerGraph");
        // Add StartEvent
        final var startEvent = new StartEvent(UUID.randomUUID().toString());
        choreographyGraph.addNode(startEvent);
        for (int i = 0; i < spanContainerGraph.nodes().size(); i++) {
            final var spanContainer = List.copyOf(spanContainerGraph.nodes()).get(i);
            log.info("SpanContainer: " + spanContainer);
            final var existsChoreographyTaskForSpanContainer = spanContainersWithChoreographyShape.containsKey(spanContainer);
            ChoreographyShape choreographyShape = null;
            if (!existsChoreographyTaskForSpanContainer) {
                if (i == 0) {
                    // Creating first ChoreographyTask
                    choreographyShape = SpanContainerToChoreographyTaskUtil.createFirstChoreographyTaskFromSpanContainer(
                            spanContainer, mapOfDetectedProcessesInTrace, participantHashSet, messageHashSet, messageMessageFlowMap);
                } else {
                    choreographyShape = SpanContainerToChoreographyTaskUtil.createChoreographyTaskFromSpanContainer(
                            spanContainer, mapOfDetectedProcessesInTrace, participantHashSet, messageHashSet, messageMessageFlowMap);
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
                choreographyShape = new ParallelGateway(UUID.randomUUID().toString());
                final var sequenceFlow = new SequenceFlow(UUID.randomUUID().toString());
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
        final var secondChoreographyShape = spanContainersWithChoreographyShape.get(List.copyOf(spanContainerGraph.nodes()).get(1));
        choreographyGraph.putEdge(startEvent, secondChoreographyShape);
    }

    private void createChoreographyShapeForSuccessors(final ChoreographyShape choreographyShape,
                                                      final SpanContainer spanContainer) {
        for (final var successor : spanContainerGraph.successors(spanContainer)) {
            ChoreographyTask successorChoreographyTask =
                    (ChoreographyTask) spanContainersWithChoreographyShape.get(successor);
            final var sequenceFlow = new SequenceFlow(UUID.randomUUID().toString());
            if (successorChoreographyTask == null) {
                log.info("Creating ChoreographyTask for successor: " + successor);
                successorChoreographyTask = SpanContainerToChoreographyTaskUtil.createChoreographyTaskFromSpanContainer(
                        successor,
                        mapOfDetectedProcessesInTrace,
                        participantHashSet,
                        messageHashSet,
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
        // Iterate over each level of the graph

    }
}

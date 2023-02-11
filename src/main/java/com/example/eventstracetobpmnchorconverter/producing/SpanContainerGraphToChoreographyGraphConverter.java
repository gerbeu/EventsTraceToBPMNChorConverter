package com.example.eventstracetobpmnchorconverter.producing;

import com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.Message;
import com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.choreography.ChoreographyShape;
import com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.choreography.MessageFlow;
import com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.choreography.Participant;
import com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.choreography.SequenceFlow;
import com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.choreography.choreographytask.ChoreographyTask;
import com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.choreography.events.StartEvent;
import com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.choreography.gateway.Gateway;
import com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.choreography.gateway.ParallelGateway;
import com.example.eventstracetobpmnchorconverter.producing.graph.SpanContainer;
import com.example.eventstracetobpmnchorconverter.util.SpanContainerGraphUtils;
import com.example.eventstracetobpmnchorconverter.util.SpanContainerToChoreographyTaskUtil;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class SpanContainerGraphToChoreographyGraphConverter {


    public void convertSpanContainerGraphToChoreographyGraph(final ImmutableGraph<SpanContainer> spanContainerGraph,
                                                             final Map<String, String> mapOfDetectedProcessesInTrace) {
        log.info("Creating ChoreographyGraph from SpanContainerGraph");

        // Create choreographyGraph
        final var choreographyGraph = GraphBuilder.directed().build();

        // Add StartEvent
        final var startEvent = new StartEvent(UUID.randomUUID().toString());
        choreographyGraph.addNode(startEvent);

        final Set<Participant> participantHashSet = new HashSet<>();
        final Set<Message> messageHashSet = new HashSet<>();
        final Map<Message, MessageFlow> messageMessageFlowMap = new HashMap<>();
        final Map<SpanContainer, ChoreographyShape> spanContainersWithChoreographyShape = new HashMap<>();
        final Set<SequenceFlow> sequenceFlowHashSet = new HashSet<>();

        for (int i = 0; i < spanContainerGraph.nodes().size(); i++) {
            final var spanContainer = List.copyOf(spanContainerGraph.nodes()).get(i);
            log.info("SpanContainer: " + spanContainer);
            final var existsChoreographyTaskForSpanContainer = spanContainersWithChoreographyShape.containsKey(spanContainer);
            ChoreographyShape choreographyShape = null;
            if(!existsChoreographyTaskForSpanContainer) {
                if(i == 0) {
                    // Creating first ChoreographyTask
                    choreographyShape = SpanContainerToChoreographyTaskUtil.createFirstChoreographyTaskFromSpanContainer(
                            spanContainer,
                            mapOfDetectedProcessesInTrace,
                            participantHashSet,
                            messageHashSet,
                            messageMessageFlowMap);
                } else {
                    choreographyShape = SpanContainerToChoreographyTaskUtil.createChoreographyTaskFromSpanContainer(
                            spanContainer,
                            mapOfDetectedProcessesInTrace,
                            participantHashSet,
                            messageHashSet,
                            messageMessageFlowMap);
                }
            } else {
                choreographyShape = (ChoreographyTask) spanContainersWithChoreographyShape.get(spanContainer);
            }
            spanContainersWithChoreographyShape.put(spanContainer, choreographyShape);
            final var hasSpanContainerSuccessorsBelongingToDifferentProcesses =
                    SpanContainerGraphUtils.hasSpanContainerSuccessorsBelongingToDifferentProcesses(
                    spanContainer,
                    spanContainerGraph);
            if(hasSpanContainerSuccessorsBelongingToDifferentProcesses) {
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
                    // TODO check
                    sequenceFlow.setSourceRef(choreographyShape.getId());
                    sequenceFlow.setTargetRef(successorChoreographyTask.getId());
                    if(choreographyShape instanceof Gateway) {
                        ((ParallelGateway) choreographyShape).addOutgoingFlow(sequenceFlow.getId());
                    } else if(choreographyShape instanceof ChoreographyTask) {
                        ((ChoreographyTask) choreographyShape).setOutgoingFlow(sequenceFlow.getId());
                    }
                    else {
                        throw new RuntimeException("ChoreographyShape is not a ChoreographyTask or a Gateway");
                    }
                    // TODO END CHECK
                    successorChoreographyTask.setIncomingFlow(sequenceFlow.getId());
                    sequenceFlowHashSet.add(sequenceFlow);
                    choreographyGraph.putEdge(choreographyShape, successorChoreographyTask);
                    spanContainersWithChoreographyShape.put(successor, successorChoreographyTask);
                } else {
                    sequenceFlow.setSourceRef(choreographyShape.getId());
                    sequenceFlow.setTargetRef(successorChoreographyTask.getId());
                    if(choreographyShape instanceof Gateway) {
                        ((ParallelGateway) choreographyShape).addOutgoingFlow(sequenceFlow.getId());
                    } else if(choreographyShape instanceof ChoreographyTask) {
                        ((ChoreographyTask) choreographyShape).setOutgoingFlow(sequenceFlow.getId());
                    }
                    else {
                        throw new RuntimeException("ChoreographyShape is not a ChoreographyTask or a Gateway");
                    }
                    successorChoreographyTask.setIncomingFlow(sequenceFlow.getId());
                    sequenceFlowHashSet.add(sequenceFlow);
                    choreographyGraph.putEdge(choreographyShape, successorChoreographyTask);
                }
            }
        }
        System.out.println(choreographyGraph);
        // Print Edges
        System.out.println("PRINTING EDGES");
        for (final var edge : choreographyGraph.edges()) {
            System.out.println(edge);
        }
    }
}

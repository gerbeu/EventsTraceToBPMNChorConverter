package com.example.eventstracetobpmnchorconverter.producing;

import com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.Message;
import com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.choreography.MessageFlow;
import com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.choreography.Participant;
import com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.choreography.choreographytask.ChoreographyTask;
import com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.choreography.events.StartEvent;
import com.example.eventstracetobpmnchorconverter.producing.graph.SpanContainer;
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
        final Map<SpanContainer, ChoreographyTask> spanContainersWithChoreographyTasks = new HashMap<>();

        for (int i = 1; i < spanContainerGraph.nodes().size(); i++) {
            final var spanContainer = List.copyOf(spanContainerGraph.nodes()).get(i);
            log.info("SpanContainer: " + spanContainer);

            ChoreographyTask choreographyTask = spanContainersWithChoreographyTasks.get(spanContainer);
            if (choreographyTask == null) {
                log.info("Creating ChoreographyTask for spanContainer: " + spanContainer);
                choreographyTask = SpanContainerToChoreographyTaskUtil.createChoreographyTaskFromSpanContainer(
                        spanContainer,
                        mapOfDetectedProcessesInTrace,
                        participantHashSet,
                        messageHashSet,
                        messageMessageFlowMap);
                spanContainersWithChoreographyTasks.put(spanContainer, choreographyTask);
            }

            for (final var successor : spanContainerGraph.successors(spanContainer)) {
                ChoreographyTask successorChoreographyTask = spanContainersWithChoreographyTasks.get(successor);
                if (successorChoreographyTask == null) {
                    log.info("Creating ChoreographyTask for successor: " + successor);
                    successorChoreographyTask = SpanContainerToChoreographyTaskUtil.createChoreographyTaskFromSpanContainer(
                            successor,
                            mapOfDetectedProcessesInTrace,
                            participantHashSet,
                            messageHashSet,
                            messageMessageFlowMap);
                    choreographyGraph.putEdge(choreographyTask, successorChoreographyTask);
                    spanContainersWithChoreographyTasks.put(successor, successorChoreographyTask);
                } else {
                    choreographyGraph.putEdge(choreographyTask, successorChoreographyTask);
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

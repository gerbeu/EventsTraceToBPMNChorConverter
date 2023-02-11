package com.example.eventstracetobpmnchorconverter.producing;

import com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.Message;
import com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.choreography.MessageFlow;
import com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.choreography.Participant;
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
        Object lastElement = startEvent;
        final var spanContainersWhichAlreadyHaveAChoreographyTask = new HashSet<SpanContainer>();
        // TODO create first choreography task
        for (int i = 1; i < spanContainerGraph.nodes().size(); i++) {
            final var spanContainer = List.copyOf(spanContainerGraph.nodes()).get(i);
            log.info("SpanContainer: " + spanContainer);
            if(!spanContainersWhichAlreadyHaveAChoreographyTask.contains(spanContainer)) {
                log.info("Creating ChoreographyTask for spanContainer: " + spanContainer);
                // Create ChoreographyTask
                final var choreographyTask = SpanContainerToChoreographyTaskUtil.createChoreographyTaskFromSpanContainer(
                        spanContainer,
                        mapOfDetectedProcessesInTrace,
                        participantHashSet,
                        messageHashSet,
                        messageMessageFlowMap);
                spanContainersWhichAlreadyHaveAChoreographyTask.add(spanContainer);
                for (final var successor : spanContainerGraph.successors(spanContainer)) {
                    if(!spanContainersWhichAlreadyHaveAChoreographyTask.contains(successor)) {
                        log.info("Creating ChoreographyTask for successor: " + successor);
                        final var successorChoreographyTask = SpanContainerToChoreographyTaskUtil.createChoreographyTaskFromSpanContainer(
                                successor,
                                mapOfDetectedProcessesInTrace,
                                participantHashSet,
                                messageHashSet,
                                messageMessageFlowMap);
                        choreographyGraph.putEdge(choreographyTask, successorChoreographyTask);
                        spanContainersWhichAlreadyHaveAChoreographyTask.add(successor);
                    }
                }
            }
        }
        System.out.println(choreographyGraph);
        // Print Edges
        System.out.println("PRINTING EDGES");
        for (final var edge : choreographyGraph.edges()) {
            System.out.println(edge);
        }


//        spanContainerGraph.nodes().stream().skip(1).forEach(spanContainer -> {
//            System.out.println("Creating ChoreographyTask for spanContainer: " + spanContainer);
//            // Create ChoreographyTask
//            final var choreographyTask = SpanContainerToChoreographyTaskUtil.createChoreographyTaskFromSpanContainer(
//                    spanContainer,
//                    mapOfDetectedProcessesInTrace,
//                    participantHashSet,
//                    messageHashSet,
//                    messageMessageFlowMap);
//            System.out.println("Created ChoreographyTask: " + choreographyTask);
//            // Add ChoreographyTask to choreographyGraph
//            choreographyGraph.addNode(choreographyTask);
//            choreographyGraph.putEdge(lastElement, choreographyTask);
//            lastElement = choreographyTask;
//        });
//        System.out.println("ChoreographyGraph: " + choreographyGraph);
    }


}

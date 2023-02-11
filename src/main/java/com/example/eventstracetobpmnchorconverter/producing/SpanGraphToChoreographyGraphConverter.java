package com.example.eventstracetobpmnchorconverter.producing;

import com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.Message;
import com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.choreography.MessageFlow;
import com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.choreography.Participant;
import com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.choreography.choreographytask.ChoreographyTask;
import com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.choreography.events.StartEvent;
import com.example.eventstracetobpmnchorconverter.jaegerTrace.spans.Span;
import com.example.eventstracetobpmnchorconverter.util.GuavaGraphMaximumDepthUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;
import java.util.*;

@Slf4j
public class SpanGraphToChoreographyGraphConverter {


    private final Set<Participant> participantHashSet = new HashSet<>();
    ;

    private final List<Message> messageList = new ArrayList<>();

    private final Map<Message, MessageFlow> messageMessageFlowMap = new HashMap<>();

    private final Map<String, String> mapOfDetectedProcessesInTrace;

    private final Set<String> setOfDetectedTopicsInTrace;

    public SpanGraphToChoreographyGraphConverter(Map<String, String> mapOfDetectedProcessesInTrace, Set<String> setOfDetectedTopicsInTrace) {
        this.mapOfDetectedProcessesInTrace = mapOfDetectedProcessesInTrace;
        this.setOfDetectedTopicsInTrace = setOfDetectedTopicsInTrace;
    }
}
//
//    public void convertSpanGraphToChoreographyGraph(final ImmutableGraph<Span> spanGraph) {
//        log.info("Converting SpanContainerGraph to ChoreographyGraph");
//        // Create choreographyGraph
//        final var choreographyGraph = GraphBuilder.directed().build();
//        // Add StartEvent
//        final var startEvent = new StartEvent(UUID.randomUUID().toString());
//        choreographyGraph.addNode(startEvent);
//        // Create firstChoreographyTask
//        final var spanNodes = spanGraph.nodes();
//        final var spanNodeIterator = spanNodes.iterator();
//        final var firstSpan = spanNodeIterator.next();
//        // The initiating participant of the first choreography task is always the user because he is the one who
//        // started the process
//        final var firstChoreographyTask = createFirstChoreographyTask(firstSpan);
//        choreographyGraph.addNode(firstChoreographyTask);
//        choreographyGraph.putEdge(startEvent, firstChoreographyTask);
//        // Create Remaining ChoreographyTasks
//        var lastChoreographyTask = firstChoreographyTask;
//        // print all span graph edges
//        System.out.println("SpanGraph edges: ");
//        // Iterate over each depth
//        // TODO
////        final var depthOfSpanGraph = GuavaGraphMaximumDepthUtil.getMaximumDepth(spanGraph, firstSpan);
////        System.out.println("Depth of SpanGraph: " + depthOfSpanGraph);
//
//        GuavaGraphMaximumDepthUtil.printNodesByDepth(spanGraph);
////        while (spanNodeIterator.hasNext()) {
////            System.out.println("Span: " + span);
////            final var spanIsAnEventMarker = isSpanAnEventMarker(span);
////            if (spanIsAnEventMarker) {
////                final var choreographyTask = createNextChoreographyTask(spanGraph, span, lastChoreographyTask);
////                choreographyGraph.addNode(choreographyTask);
////                choreographyGraph.putEdge(lastChoreographyTask, choreographyTask);
////                lastChoreographyTask = choreographyTask;
////            }
////        }
//
//        System.out.println("---------------------");
//        // Print ChoreographyGraph
//        System.out.println("ChoreographyGraph: " + choreographyGraph);
//        for(var node : choreographyGraph.nodes()) {
//            System.out.println("Node: " + node);
//        }
//        // Print all edges
//        for (var edge : choreographyGraph.edges()) {
//            System.out.println("Edge: " + edge);
//        }
//
//    }
//
//
//
//    public static <N> void printChildren(Graph<N> graph, N node) {
//        Set<N> children = graph.successors(node);
//        System.out.println("Children of node " + node + ": " + children);
//    }
//
//    private boolean isSpanAnEventMarker(final Span span) {
//        final var relatedSpanContainsMessagingSystemTag = Arrays.stream(span.getTags()).anyMatch(tag -> tag.getKey()
//                .equals("produced.event"));
//        final var relatedSpanContainsMessagingDestinationTag = Arrays.stream(span.getTags()).anyMatch(tag -> tag
//                .getKey().equals("processed.event"));
//        return relatedSpanContainsMessagingSystemTag || relatedSpanContainsMessagingDestinationTag;
//    }
//
//    private ChoreographyTask createFirstChoreographyTask(final Span firstSpan) {
//        log.info("Creating first choreography task");
//        // The initiating participant of the first choreography task is always the user because he is the one who
//        // started the process
//        final var initiatingParticipant = new Participant(UUID.randomUUID().toString(), "user");
//        participantHashSet.add(initiatingParticipant);
//        final var receivingParticipant = createReceivingParticipant(firstSpan);
//        // The first choreography task does not contain a message -> Empty Message
//        final var message = new Message(UUID.randomUUID().toString(), "");
//        messageList.add(message);
//        // Create messageFlow from initiating participant to receiving participant
//        final var messageFlow = new MessageFlow(UUID.randomUUID().toString(),
//                message.getId(),
//                initiatingParticipant.getId(),
//                receivingParticipant.getId());
//        messageMessageFlowMap.put(message, messageFlow);
//        return new ChoreographyTask(
//                UUID.randomUUID().toString(),
//                firstSpan.getOperationName(),
//                initiatingParticipant, receivingParticipant, initiatingParticipant.getId(),
//                receivingParticipant.getId(),
//                List.of(initiatingParticipant.getId(), receivingParticipant.getId()),
//                List.of(messageFlow.getId())
//        );
//    }
//
//    private ChoreographyTask createNextChoreographyTask(final ImmutableGraph<Span> spanGraph,
//                                                        final Span span,
//                                                        final ChoreographyTask parentChoreographyTask) {
//        final var spanContainsProducedEventTag = Arrays.stream(span.getTags()).anyMatch(tag -> tag.getKey().equals(
//                "produced.event"));
//        final var spanContainsProcessedEventTag = Arrays.stream(span.getTags()).anyMatch(tag -> tag.getKey().equals(
//                "processed.event"));
//        if (spanContainsProducedEventTag) {
//            // Create PRODUCES-CHOREOGRAPHY-TASK
//            return createProducedChoreographyTask(spanGraph, span, parentChoreographyTask);
//
//        } else if (spanContainsProcessedEventTag) {
//            // Create PROCESSES-CHOREOGRAPHY-TASK
//            return createProcessedChoreographyTask(spanGraph, span, parentChoreographyTask);
//
//        } else {
//            // ERROR
//            throw new RuntimeException("Span does not contain a produced or processed event tag");
//        }
//    }
//
//    private ChoreographyTask createProducedChoreographyTask(final ImmutableGraph<Span> spanGraph,
//                                                            final Span span,
//                                                            final ChoreographyTask parentChoreographyTask) {
//        log.info("Creating produced choreography task");
//        // Check if child span has Tag "messaging.system" and "messaging.destination"
//        if (!checkIfCreationOfProducedChoreographyTaskIsPossible(spanGraph, span)) {
//            return null;
//        }
//        final var initiatingParticipantRef = parentChoreographyTask.getInitiatingParticipantRef();
//        final var childSpan = spanGraph.successors(span).iterator().next();
//        // TODO
//        final var TopicNameOfPublishedEvent = Arrays.stream(childSpan.getTags())
//                .filter(tag -> tag.getKey().equals("messaging.destination"))
//                .findFirst()
//                .get()
//                .getValue();
//        // TODO
//        final var receivingParticipant = new Participant(UUID.randomUUID().toString(), TopicNameOfPublishedEvent);
//        final var message = createProducedMessageFromEventInSpan(span);
//        final var messageFlow = createMessageFlow(initiatingParticipantRef, receivingParticipant, message);
//        return createChoreographyTask(span, initiatingParticipantRef, receivingParticipant, messageFlow);
//    }
//
//    /**
//     * Checks if the span has only one parent span which contains the tags "messaging.system" and "messaging
//     * .destination"
//     *
//     * @param spanGraph
//     * @param span
//     * @return
//     */
//    private boolean checkIfCreationOfProducedChoreographyTaskIsPossible(final ImmutableGraph<Span> spanGraph,
//                                                                        final Span span) {
//        // Because the child span gets checked -> Predecessor = false
//        return checkSpan(spanGraph, span, false);
//    }
//
//    private boolean checkSpan(final ImmutableGraph<Span> spanGraph,
//                              final Span span,
//                              final boolean isPredecessor) {
//        final var relatedSpans = isPredecessor ? spanGraph.predecessors(span) : spanGraph.successors(span);
//        if (relatedSpans.size() != 1) {
//            return false;
//        }
//        final var relatedSpan = relatedSpans.iterator().next();
//        final var relatedSpanContainsMessagingSystemTag = Arrays.stream(relatedSpan.getTags()).anyMatch(tag -> tag.getKey()
//                .equals("messaging.system"));
//        if (!relatedSpanContainsMessagingSystemTag) {
//            return false;
//        }
//        final var relatedSpanContainsMessagingDestinationTag = Arrays.stream(relatedSpan.getTags()).anyMatch(tag -> tag
//                .getKey().equals("messaging.destination"));
//        if (!relatedSpanContainsMessagingDestinationTag) {
//            return false;
//        }
//        return true;
//    }
//
//
//    private ChoreographyTask createProcessedChoreographyTask(final ImmutableGraph<Span> spanGraph, final Span span,
//                                                             final ChoreographyTask parentChoreographyTask) {
//        log.info("Creating processed choreography task");
//        if (!checkIfCreationOfProcessedChoreographyTaskIsPossible(spanGraph, span)) {
//            return null;
//        }
//        final var initiatingParticipantRef = parentChoreographyTask.getInitiatingParticipantRef();
//        final var receivingParticipant = createReceivingParticipant(span);
//        final var message = createProcessedMessageFromEventInSpan(span);
//        final var messageFlow = createMessageFlow(initiatingParticipantRef, receivingParticipant, message);
//        return createChoreographyTask(span, initiatingParticipantRef, receivingParticipant, messageFlow);
//    }
//
//    /**
//     * Checks if the span has only one parent span which contains the tags "messaging.system" and "messaging
//     * .destination"
//     *
//     * @param spanGraph
//     * @param span
//     * @return
//     */
//    private boolean checkIfCreationOfProcessedChoreographyTaskIsPossible(final ImmutableGraph<Span> spanGraph,
//                                                                         final Span span) {
//        // Because the parent span gets checked -> Predecessor = true
//        return checkSpan(spanGraph, span, true);
//    }
//
//    private Participant createReceivingParticipant(Span span) {
//        final var nameOfMicroserviceOfSpan = mapOfDetectedProcessesInTrace.get(span.getProcessID());
//        final var receivingParticipant = new Participant(UUID.randomUUID().toString(), nameOfMicroserviceOfSpan);
//        participantHashSet.add(receivingParticipant);
//        return receivingParticipant;
//    }
//
//
//    private Message createProducedMessageFromEventInSpan(final Span span) {
//        final var requiredTag = "produced.event";
//        final var eventString = Arrays.stream(span.getTags())
//                .filter(tag -> tag.getKey().equals(requiredTag))
//                .findFirst()
//                .orElseThrow(() -> new RuntimeException(MessageFormat.format("Span {0} does not contain a {1} tag",
//                        span.getSpanID(), requiredTag)))
//                .getValue();
//        final var objectMapper = new ObjectMapper();
//        try {
//            final var event = objectMapper.readValue(eventString, Event.class);
//            final var message = new Message(UUID.randomUUID().toString(), event.getType());
//            messageList.add(message);
//            return message;
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private Message createProcessedMessageFromEventInSpan(Span span) {
//        final var requiredTag = "processed.event";
//        final var eventString = Arrays.stream(span.getTags())
//                .filter(tag -> tag.getKey().equals(requiredTag))
//                .findFirst()
//                .orElseThrow(() -> new RuntimeException(MessageFormat.format("Span {0} does not contain a {1} tag",
//                        span.getSpanID(), requiredTag)))
//                .getValue();
//        final var objectMapper = new ObjectMapper();
//        try {
//            final var event = objectMapper.readValue(eventString, Event.class);
//            final var message = new Message(UUID.randomUUID().toString(), event.getType());
//            messageList.add(message);
//            return message;
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private MessageFlow createMessageFlow(String initiatingParticipantRef, Participant receivingParticipant, Message message) {
//        final var messageFlow = new MessageFlow(UUID.randomUUID().toString(),
//                message.getId(),
//                initiatingParticipantRef,
//                receivingParticipant.getId());
//        messageMessageFlowMap.put(message, messageFlow);
//        return messageFlow;
//    }
//
//    private ChoreographyTask createChoreographyTask(Span span, String initiatingParticipantRef, Participant receivingParticipant, MessageFlow messageFlow) {
//        return null;
////        return new ChoreographyTask(
////                UUID.randomUUID().toString(),
////                span.getOperationName(),
////                initiatingParticipant, receivingParticipant, initiatingParticipantRef,
////                receivingParticipant.getId(),
////                List.of(initiatingParticipantRef, receivingParticipant.getId()),
////                List.of(messageFlow.getId())
////        );
//    }
//
//
//    // END CREATE PROCESSED CHOREOGRAPHY TASK BLOCK
//}

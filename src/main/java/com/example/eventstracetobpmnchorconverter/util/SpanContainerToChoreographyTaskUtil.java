package com.example.eventstracetobpmnchorconverter.util;

import com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.Message;
import com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.choreography.MessageFlow;
import com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.choreography.Participant;
import com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.choreography.choreographytask.ChoreographyTask;
import com.example.eventstracetobpmnchorconverter.jaegerTrace.spans.Span;
import com.example.eventstracetobpmnchorconverter.producing.Event;
import com.example.eventstracetobpmnchorconverter.producing.graph.SpanContainer;
import com.example.eventstracetobpmnchorconverter.producing.graph.SpanEventTuple;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;
import java.util.*;

@Slf4j
public class SpanContainerToChoreographyTaskUtil {

    private static final String PUBLISHING_EVENT = "publishing event";

    private static final String PROCESSING_EVENT = "processing event";

    private static final String PRODUCED_EVENT_TAG_KEY = "produced.event";

    private static final String PROCESSED_EVENT_TAG_KEY = "processed.event";

    public static ChoreographyTask createFirstChoreographyTaskFromSpanContainer(final SpanContainer spanContainer,
                                                                               final Map<String, String> mapOfDetectedProcessesInTrace,
                                                                               final Set<Participant> choreographyParticipants,
                                                                               final Set<Message> messageSet,
                                                                               final Map<Message, MessageFlow> messageMessageFlowMap) {
        final var spanContainerIsSimpleSpan = spanContainer.getSpanComponent() instanceof Span;
        if (spanContainerIsSimpleSpan) {
            return createFirstChoreographyTaskForSimpleSpan(spanContainer, mapOfDetectedProcessesInTrace, choreographyParticipants, messageSet, messageMessageFlowMap);

        }
        final var spanContainerIsSpanEventTuple = spanContainer.getSpanComponent() instanceof SpanEventTuple;
        if (!spanContainerIsSpanEventTuple) {
            return createChoreographyTaskFromSpanContainer(spanContainer, mapOfDetectedProcessesInTrace, choreographyParticipants, messageSet, messageMessageFlowMap);
        }
        throw new RuntimeException("SpanContainer is a SpanEventTuple");
    }

    private static ChoreographyTask createFirstChoreographyTaskForSimpleSpan(SpanContainer spanContainer,
                                                                             Map<String, String> mapOfDetectedProcessesInTrace,
                                                                             Set<Participant> choreographyParticipants,
                                                                             Set<Message> messageSet,
                                                                             Map<Message, MessageFlow> messageMessageFlowMap) {
        final var choreographyTaskId = UUID.randomUUID().toString();
        final var span = (Span) spanContainer.getSpanComponent();
        final var choreographyTaskName = span.getOperationName();
        final var initiatingParticipant = new Participant(UUID.randomUUID().toString(), "user");
        choreographyParticipants.add(initiatingParticipant);
        final var initiatingParticipantRef = initiatingParticipant.getId();
        final var nameOfMicroserviceOfSpan = mapOfDetectedProcessesInTrace.get(span.getProcessID());
        final var receivingParticipant = new Participant(UUID.randomUUID().toString(), nameOfMicroserviceOfSpan);
        choreographyParticipants.add(receivingParticipant);
        final var receivingParticipantRef = receivingParticipant.getId();
        final var participantRefs = List.of(initiatingParticipantRef, receivingParticipantRef);
        final var message = new Message(UUID.randomUUID().toString(), "");
        messageSet.add(message);
        final var messageFlow = new MessageFlow(UUID.randomUUID().toString(),
                message.getId(),
                initiatingParticipant.getId(),
                receivingParticipant.getId());
        messageMessageFlowMap.put(message, messageFlow);
        final var messageFlowRefs = List.of(messageFlow.getId());
        return new ChoreographyTask(choreographyTaskId, choreographyTaskName, initiatingParticipantRef,
                participantRefs, messageFlowRefs);
    }


    public static ChoreographyTask createChoreographyTaskFromSpanContainer(final SpanContainer spanContainer,
                                                                           final Map<String, String> mapOfDetectedProcessesInTrace,
                                                                           final Set<Participant> choreographyParticipants,
                                                                           final Set<Message> messageSet,
                                                                           final Map<Message, MessageFlow> messageMessageFlowMap) {
        final var spanContainerIsSpanEventTuple = spanContainer.getSpanComponent() instanceof SpanEventTuple;
        if (!spanContainerIsSpanEventTuple) {
            throw new RuntimeException("SpanContainer is not a SpanEventTuple");
        }
        final var spanEventTuple = (SpanEventTuple) spanContainer.getSpanComponent();
        final var containsSpanEventTupleProducerMarkerSpan = SpanEventTupleUtil.containsSpanEventTupleProducerMarkerSpan(spanEventTuple);
        if (containsSpanEventTupleProducerMarkerSpan) {
            return createChoreographyTaskFromSpanEventTupleProducerMarkerSpan(spanEventTuple,
                    mapOfDetectedProcessesInTrace, choreographyParticipants, messageSet, messageMessageFlowMap);
        }
        final var containsSpanEventTupleProcessorMarkerSpan = SpanEventTupleUtil.containsSpanEventTupleProcessorMarkerSpan(spanEventTuple);
        if (containsSpanEventTupleProcessorMarkerSpan) {
            return createChoreographyTaskFromSpanEventTupleProcessorMarkerSpan(spanEventTuple,
                    mapOfDetectedProcessesInTrace, choreographyParticipants, messageSet, messageMessageFlowMap);
        }
        throw new RuntimeException("SpanEventTuple does not contain a ProducerMarkerSpan or a ProcessorMarkerSpan");
    }

    private static ChoreographyTask createChoreographyTaskFromSpanEventTupleProducerMarkerSpan(SpanEventTuple spanEventTuple,
                                                                                               final Map<String,
                                                                                                       String> mapOfDetectedProcessesInTrace,
                                                                                               final Set<Participant> choreographyParticipants,
                                                                                               final Set<Message> messageSet,
                                                                                               final Map<Message, MessageFlow> messageMessageFlowMap) {
        log.info("Creating ChoreographyTask from SpanEventTupleProducerMarkerSpan");
        final var choreographyTaskId = UUID.randomUUID().toString();
        final var initiatingParticipant = getOrCreateParticipantFromMicroservice(spanEventTuple, mapOfDetectedProcessesInTrace,
                choreographyParticipants);
        final var initiatingParticipantRef = initiatingParticipant.getId();
        final var receivingParticipant = createReceivingParticipantFromSpanEventTupleProducerMarkerSpan(spanEventTuple,
                choreographyParticipants);
        final var receivingParticipantRef = receivingParticipant.getId();
        final var participantRefs = List.of(initiatingParticipantRef, receivingParticipantRef);
        final var message = createMessageFromSpanEventTupleProducerMarkerSpan(spanEventTuple, messageSet);
        final var messageFlow = MessageFlowUtil.createMessageFlow(initiatingParticipant, receivingParticipant, message,
                messageMessageFlowMap);
        final var messageFlowRefs = List.of(messageFlow.getId());
        return new ChoreographyTask(choreographyTaskId, PUBLISHING_EVENT, initiatingParticipantRef, participantRefs,
                messageFlowRefs);
    }


    private static Message createMessageFromSpanEventTupleProducerMarkerSpan(SpanEventTuple spanEventTuple,
                                                                             Set<Message> messageSet) {
        final var spanToGetEventFrom = spanEventTuple.getFirstSpan();
        final var eventString = Arrays.stream(spanToGetEventFrom.getTags())
                .filter(tag -> tag.getKey().equals(PRODUCED_EVENT_TAG_KEY))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(MessageFormat.format("Span {0} does not contain a {1} tag",
                        spanToGetEventFrom.getSpanID(), PRODUCED_EVENT_TAG_KEY)))
                .getValue();
        final var objectMapper = new ObjectMapper();
        try {
            final var event = objectMapper.readValue(eventString, Event.class);
            final var message = new Message(UUID.randomUUID().toString(), event.getType());
            messageSet.add(message);
            return message;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    private static ChoreographyTask createChoreographyTaskFromSpanEventTupleProcessorMarkerSpan(SpanEventTuple spanEventTuple,
                                                                                                final Map<String, String> mapOfDetectedProcessesInTrace,
                                                                                                final Set<Participant> choreographyParticipants,
                                                                                                final Set<Message> messageSet,
                                                                                                final Map<Message, MessageFlow> messageMessageFlowMap) {
        log.info("Creating ChoreographyTask from SpanEventTupleProcessorMarkerSpan");
        final var choreographyTaskId = UUID.randomUUID().toString();
        final var initiatingParticipant =
                createInitiatingParticipantFromSpanEventTupleProcessorMarkerSpan(spanEventTuple,
                        mapOfDetectedProcessesInTrace, choreographyParticipants);
        final var initiatingParticipantRef = initiatingParticipant.getId();
        final var receivingParticipant = getOrCreateParticipantFromMicroservice(spanEventTuple,
                mapOfDetectedProcessesInTrace, choreographyParticipants);
        final var receivingParticipantRef = receivingParticipant.getId();
        final var participantRefs = List.of(initiatingParticipantRef, receivingParticipantRef);
        final var message = createMessageFromSpanEventTupleProcessorMarkerSpan(spanEventTuple, messageSet);
        final var messageFlow = MessageFlowUtil.createMessageFlow(initiatingParticipant, receivingParticipant, message,
                messageMessageFlowMap);
        final var messageFlowRefs = List.of(messageFlow.getId());
        return new ChoreographyTask(choreographyTaskId, PROCESSING_EVENT, initiatingParticipantRef, participantRefs,
                messageFlowRefs);


    }

    private static Message createMessageFromSpanEventTupleProcessorMarkerSpan(SpanEventTuple spanEventTuple, Set<Message> messageSet) {
        final var spanToGetEventFrom = spanEventTuple.getSecondSpan();
        final var eventString = Arrays.stream(spanToGetEventFrom.getTags())
                .filter(tag -> tag.getKey().equals(PROCESSED_EVENT_TAG_KEY))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(MessageFormat.format("Span {0} does not contain a {1} tag",
                        spanToGetEventFrom.getSpanID(), PROCESSED_EVENT_TAG_KEY)))
                .getValue();
        final var objectMapper = new ObjectMapper();
        try {
            final var event = objectMapper.readValue(eventString, Event.class);
            final var message = new Message(UUID.randomUUID().toString(), event.getType());
            messageSet.add(message);
            return message;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    private static Participant createInitiatingParticipantFromSpanEventTupleProcessorMarkerSpan(SpanEventTuple spanEventTuple,
                                                                                                final Map<String, String> mapOfDetectedProcessesInTrace,
                                                                                                final Set<Participant> choreographyParticipants) {
        return getOrCreateParticipantForMessagingMarkerSpan(spanEventTuple.getFirstSpan(), choreographyParticipants);
    }

    private static Participant createReceivingParticipantFromSpanEventTupleProducerMarkerSpan(SpanEventTuple spanEventTuple,
                                                                                              final Set<Participant> choreographyParticipants) {
        return getOrCreateParticipantForMessagingMarkerSpan(spanEventTuple.getSecondSpan(), choreographyParticipants);
    }

    private static Participant getOrCreateParticipantForMessagingMarkerSpan(Span span,
                                                                            Set<Participant> choreographyParticipants) {
        final var messagingDestination = Arrays.stream(span.getTags())
                .filter(tag -> tag.getKey().equals("messaging.destination"))
                .findFirst()
                .orElseThrow()
                .getValue();
        final var messagingDestinationKind = Arrays.stream(span.getTags())
                .filter(tag -> tag.getKey().equals("messaging.destination_kind"))
                .findFirst()
                .orElseThrow()
                .getValue();
        final var messagingSystem = Arrays.stream(span.getTags())
                .filter(tag -> tag.getKey().equals("messaging.system"))
                .findFirst()
                .orElseThrow()
                .getValue();
        final var nameOfReceivingParticipant = MessageFormat.format("{0} {1} ({2})", messagingDestination,
                messagingDestinationKind, messagingSystem);
        final var foundParticipant = choreographyParticipants.stream()
                .filter(participant -> participant.getName().equals(nameOfReceivingParticipant))
                .findFirst();
        if (foundParticipant.isPresent()) {
            return foundParticipant.get();
        }
        final var newParticipant = new Participant(UUID.randomUUID().toString(), nameOfReceivingParticipant);
        choreographyParticipants.add(newParticipant);
        return newParticipant;
    }

    private static Participant getOrCreateParticipantFromMicroservice(SpanEventTuple spanEventTuple,
                                                                      Map<String, String> mapOfDetectedProcessesInTrace,
                                                                      final Set<Participant> choreographyParticipants) {
        final var nameOfReceivingParticipant = mapOfDetectedProcessesInTrace.get(spanEventTuple.getFirstSpan().getProcessID());
        final var foundParticipant = choreographyParticipants.stream()
                .filter(participant -> participant.getName().equals(nameOfReceivingParticipant))
                .findFirst();
        if (foundParticipant.isPresent()) {
            return foundParticipant.get();
        }
        final var newParticipant = new Participant(UUID.randomUUID().toString(), nameOfReceivingParticipant);
        choreographyParticipants.add(newParticipant);
        return newParticipant;
    }


}

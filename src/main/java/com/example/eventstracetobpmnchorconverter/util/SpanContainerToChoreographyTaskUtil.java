package com.example.eventstracetobpmnchorconverter.util;

import com.example.eventstracetobpmnchorconverter.algorithm.services.MicroservicesInfoService;
import com.example.eventstracetobpmnchorconverter.algorithm.services.TopicsEventsInfoService;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.Message;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.MessageFlow;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.Participant;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.choreographytask.ChoreographyTask;
import com.example.eventstracetobpmnchorconverter.jaegerTrace.spans.Span;
import com.example.eventstracetobpmnchorconverter.producing.Event;
import com.example.eventstracetobpmnchorconverter.producing.graph.SpanContainer;
import com.example.eventstracetobpmnchorconverter.producing.graph.SpanEventTuple;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.text.MessageFormat;
import java.util.*;

@Service
@Slf4j
@RequestScope
public class SpanContainerToChoreographyTaskUtil {

    private static final String PUBLISHING_EVENT = "publishing event";

    private static final String PROCESSING_EVENT = "processing event";

    private static final String PRODUCED_EVENT_TAG_KEY = "produced.event";

    private static final String PROCESSED_EVENT_TAG_KEY = "processed.event";

    @Autowired
    private final TopicsEventsInfoService topicsEventsInfoService;

    @Autowired
    private final MicroservicesInfoService microservicesInfoService;

    public SpanContainerToChoreographyTaskUtil(TopicsEventsInfoService topicsEventsInfoService, MicroservicesInfoService microservicesInfoService) {
        this.topicsEventsInfoService = topicsEventsInfoService;
        this.microservicesInfoService = microservicesInfoService;
    }


    public ChoreographyTask createFirstChoreographyTaskFromSpanContainer(final SpanContainer spanContainer,
                                                                         final Map<String, String> mapOfDetectedProcessesInTrace,
                                                                         final Set<Participant> choreographyParticipants,
                                                                         final List<Message> messages,
                                                                         final Map<Message, MessageFlow> messageMessageFlowMap) {
        final var spanContainerIsSimpleSpan = spanContainer.getSpanComponent() instanceof Span;
        if (spanContainerIsSimpleSpan) {
            return createFirstChoreographyTaskForSimpleSpan(spanContainer, mapOfDetectedProcessesInTrace, choreographyParticipants, messages, messageMessageFlowMap);

        }
        final var spanContainerIsSpanEventTuple = spanContainer.getSpanComponent() instanceof SpanEventTuple;
        if (!spanContainerIsSpanEventTuple) {
            return createChoreographyTaskFromSpanContainer(spanContainer, mapOfDetectedProcessesInTrace, choreographyParticipants, messages, messageMessageFlowMap);
        }
        throw new RuntimeException("SpanContainer is a SpanEventTuple");
    }

    private ChoreographyTask createFirstChoreographyTaskForSimpleSpan(SpanContainer spanContainer,
                                                                      Map<String, String> mapOfDetectedProcessesInTrace,
                                                                      Set<Participant> choreographyParticipants,
                                                                      List<Message> messages,
                                                                      Map<Message, MessageFlow> messageMessageFlowMap) {
        final var choreographyTaskId = RandomIDGenerator.generateWithPrefix("ChoreographyTask");
        final var span = (Span) spanContainer.getSpanComponent();
        final var choreographyTaskName = span.getOperationName();
        final var initiatingParticipant = new Participant(RandomIDGenerator.generateWithPrefix("InitiatingParticipant"),
                "user");
        choreographyParticipants.add(initiatingParticipant);
        final var initiatingParticipantRef = initiatingParticipant.getId();
        final var nameOfMicroserviceOfSpan = mapOfDetectedProcessesInTrace.get(span.getProcessID());
        final var receivingParticipant = new Participant(RandomIDGenerator.generateWithPrefix("ReceivingParticipant"),
                nameOfMicroserviceOfSpan);
        choreographyParticipants.add(receivingParticipant);
        final var receivingParticipantRef = receivingParticipant.getId();
        final var participantRefs = List.of(initiatingParticipantRef, receivingParticipantRef);
        final var message = new Message(RandomIDGenerator.generateWithPrefix("Message"), "");
        messages.add(message);
        final var messageFlow = MessageFlow.builder()
                .id(RandomIDGenerator.generateWithPrefix("MessageFlow"))
                .messageRef(message.getId())
                .sourceRef(initiatingParticipant.getId())
                .targetRef(receivingParticipant.getId())
                .build();
//        final var messageFlow = new MessageFlow(UUID.randomUUID().toString(),
//                message.getId(),
//                initiatingParticipant.getId(),
//                receivingParticipant.getId());
        messageMessageFlowMap.put(message, messageFlow);
        final var messageFlowRefs = List.of(messageFlow.getId());
        return new ChoreographyTask(choreographyTaskId, choreographyTaskName, initiatingParticipantRef,
                participantRefs, messageFlowRefs);
    }


    public ChoreographyTask createChoreographyTaskFromSpanContainer(final SpanContainer spanContainer,
                                                                    final Map<String, String> mapOfDetectedProcessesInTrace,
                                                                    final Set<Participant> choreographyParticipants,
                                                                    final List<Message> messages,
                                                                    final Map<Message, MessageFlow> messageMessageFlowMap) {
        final var spanContainerIsSpanEventTuple = spanContainer.getSpanComponent() instanceof SpanEventTuple;
        if (!spanContainerIsSpanEventTuple) {
            throw new RuntimeException("SpanContainer is not a SpanEventTuple");
        }
        final var spanEventTuple = (SpanEventTuple) spanContainer.getSpanComponent();
        final var containsSpanEventTupleProducerMarkerSpan = SpanEventTupleUtil.containsSpanEventTupleProducerMarkerSpan(spanEventTuple);
        if (containsSpanEventTupleProducerMarkerSpan) {
            return createChoreographyTaskFromSpanEventTupleProducerMarkerSpan(spanEventTuple,
                    mapOfDetectedProcessesInTrace, choreographyParticipants, messages, messageMessageFlowMap);
        }
        final var containsSpanEventTupleProcessorMarkerSpan = SpanEventTupleUtil.containsSpanEventTupleProcessorMarkerSpan(spanEventTuple);
        if (containsSpanEventTupleProcessorMarkerSpan) {
            return createChoreographyTaskFromSpanEventTupleProcessorMarkerSpan(spanEventTuple,
                    mapOfDetectedProcessesInTrace, choreographyParticipants, messages, messageMessageFlowMap);
        }
        throw new RuntimeException("SpanEventTuple does not contain a ProducerMarkerSpan or a ProcessorMarkerSpan");
    }

    private ChoreographyTask createChoreographyTaskFromSpanEventTupleProducerMarkerSpan(SpanEventTuple spanEventTuple,
                                                                                        final Map<String,
                                                                                                String> mapOfDetectedProcessesInTrace,
                                                                                        final Set<Participant> choreographyParticipants,
                                                                                        final List<Message> messages,
                                                                                        final Map<Message, MessageFlow> messageMessageFlowMap) {
        log.info("Creating ChoreographyTask from SpanEventTupleProducerMarkerSpan");
        final var choreographyTaskId = RandomIDGenerator.generateWithPrefix("ChoreographyTask");
        final var initiatingParticipant = getOrCreateParticipantFromMicroservice(spanEventTuple, mapOfDetectedProcessesInTrace,
                choreographyParticipants);
        final var initiatingParticipantRef = initiatingParticipant.getId();
        final var receivingParticipant = createReceivingParticipantFromSpanEventTupleProducerMarkerSpan(spanEventTuple,
                choreographyParticipants);
        final var receivingParticipantRef = receivingParticipant.getId();
        final var participantRefs = List.of(initiatingParticipantRef, receivingParticipantRef);
        final var message = createMessageFromSpanEventTupleProducerMarkerSpan(spanEventTuple, messages);
        final var messageFlow = MessageFlowUtil.createMessageFlow(initiatingParticipant, receivingParticipant, message,
                messageMessageFlowMap);
        final var messageFlowRefs = List.of(messageFlow.getId());
        return new ChoreographyTask(choreographyTaskId, PUBLISHING_EVENT, initiatingParticipantRef, participantRefs,
                messageFlowRefs);
    }


    private Message createMessageFromSpanEventTupleProducerMarkerSpan(SpanEventTuple spanEventTuple,
                                                                      List<Message> messages) {
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
            final var topic =
                    Arrays.stream(spanEventTuple.getSecondSpan().getTags())
                            .filter(tag -> tag.getKey().equals("messaging.destination"))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Span does not contain a messaging.destination tag"))
                            .getValue();
            final var microserviceName = microservicesInfoService.getMicroserviceNameByProcessId(spanEventTuple.getFirstSpan().getProcessID());
            topicsEventsInfoService.addEventToTopic(eventString, topic);
            topicsEventsInfoService.addProducerToTopic(microserviceName, topic);
            final var message = new Message(RandomIDGenerator.generateWithPrefix("Message"), event.getType());
            messages.add(message);
            return message;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    private ChoreographyTask createChoreographyTaskFromSpanEventTupleProcessorMarkerSpan(SpanEventTuple spanEventTuple,
                                                                                         final Map<String, String> mapOfDetectedProcessesInTrace,
                                                                                         final Set<Participant> choreographyParticipants,
                                                                                         final List<Message> messages,
                                                                                         final Map<Message, MessageFlow> messageMessageFlowMap) {
        log.info("Creating ChoreographyTask from SpanEventTupleProcessorMarkerSpan");
        final var choreographyTaskId = RandomIDGenerator.generateWithPrefix("ChoreographyTask");
        final var initiatingParticipant =
                createInitiatingParticipantFromSpanEventTupleProcessorMarkerSpan(spanEventTuple,
                        choreographyParticipants);
        final var initiatingParticipantRef = initiatingParticipant.getId();
        final var receivingParticipant = getOrCreateParticipantFromMicroservice(spanEventTuple,
                mapOfDetectedProcessesInTrace, choreographyParticipants);
        final var receivingParticipantRef = receivingParticipant.getId();
        final var participantRefs = List.of(initiatingParticipantRef, receivingParticipantRef);
        final var message = createMessageFromSpanEventTupleProcessorMarkerSpan(spanEventTuple, messages);
        final var messageFlow = MessageFlowUtil.createMessageFlow(initiatingParticipant, receivingParticipant, message,
                messageMessageFlowMap);
        final var messageFlowRefs = List.of(messageFlow.getId());
        return new ChoreographyTask(choreographyTaskId, PROCESSING_EVENT, initiatingParticipantRef, participantRefs,
                messageFlowRefs);


    }

    private Message createMessageFromSpanEventTupleProcessorMarkerSpan(SpanEventTuple spanEventTuple,
                                                                       List<Message> messages) {
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
            final var topic =
                    Arrays.stream(spanEventTuple.getFirstSpan().getTags())
                            .filter(tag -> tag.getKey().equals("messaging.destination"))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Span does not contain a messaging.destination tag"))
                            .getValue();
            final var microserviceName = microservicesInfoService.getMicroserviceNameByProcessId(spanEventTuple.getFirstSpan().getProcessID());
            topicsEventsInfoService.addEventToTopic(eventString, topic);
            topicsEventsInfoService.addConsumerToTopic(microserviceName, topic);
            final var message = new Message(RandomIDGenerator.generateWithPrefix("Message"), event.getType());
            messages.add(message);
            return message;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    private Participant createInitiatingParticipantFromSpanEventTupleProcessorMarkerSpan(SpanEventTuple spanEventTuple,
                                                                                         final Set<Participant> choreographyParticipants) {
        return getOrCreateParticipantForMessagingMarkerSpan(spanEventTuple.getFirstSpan(), choreographyParticipants);
    }

    private Participant createReceivingParticipantFromSpanEventTupleProducerMarkerSpan(SpanEventTuple spanEventTuple,
                                                                                       final Set<Participant> choreographyParticipants) {
        return getOrCreateParticipantForMessagingMarkerSpan(spanEventTuple.getSecondSpan(), choreographyParticipants);
    }

    private Participant getOrCreateParticipantForMessagingMarkerSpan(Span span,
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
        final var newParticipant = new Participant(RandomIDGenerator.generateWithPrefix("Participant"),
                nameOfReceivingParticipant);
        choreographyParticipants.add(newParticipant);
        return newParticipant;
    }

    private Participant getOrCreateParticipantFromMicroservice(SpanEventTuple spanEventTuple,
                                                               Map<String, String> mapOfDetectedProcessesInTrace,
                                                               final Set<Participant> choreographyParticipants) {
        final var nameOfReceivingParticipant = mapOfDetectedProcessesInTrace.get(spanEventTuple.getFirstSpan().getProcessID());
        final var foundParticipant = choreographyParticipants.stream()
                .filter(participant -> participant.getName().equals(nameOfReceivingParticipant))
                .findFirst();
        if (foundParticipant.isPresent()) {
            return foundParticipant.get();
        }
        final var newParticipant = new Participant(RandomIDGenerator.generateWithPrefix("Participant"),
                nameOfReceivingParticipant);
        choreographyParticipants.add(newParticipant);
        return newParticipant;
    }


}

package com.example.eventstracetobpmnchorconverter;

import com.example.eventstracetobpmnchorconverter.jaegerTrace.Trace;
import com.example.eventstracetobpmnchorconverter.jaegerTrace.criteria.ProcessTagCriteria;
import com.example.eventstracetobpmnchorconverter.jaegerTrace.criteria.TagCriteria;
import com.example.eventstracetobpmnchorconverter.jaegerTrace.spans.Span;
import com.example.eventstracetobpmnchorconverter.producing.SpanContainerGraphToChoreographyGraphConverter;
import com.example.eventstracetobpmnchorconverter.producing.SpanGraphToChoreographyGraphConverter;
import com.example.eventstracetobpmnchorconverter.producing.graph.SpanContainer;
import com.example.eventstracetobpmnchorconverter.visitors.jaeger_trace.*;
import com.google.common.graph.ImmutableGraph;
import lombok.NoArgsConstructor;

import java.text.MessageFormat;
import java.util.*;

@NoArgsConstructor
public class Algorithm {

    private final EventsInfoVisitor eventsInfoVisitor = new EventsInfoVisitor();

    private final TopicsInfoVisitor topicsInfoVisitor = new TopicsInfoVisitor();

    private final ProcessesInfoVisitor processesInfoVisitor = new ProcessesInfoVisitor();

    private final TraceToSpanGuavaGraphVisitor traceToSpanGuavaGraphVisitor = new TraceToSpanGuavaGraphVisitor();

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
        continueV3(trace, mapOfDetectedProcessesInTrace, setOfDetectedTopicsInTrace);
    }
//
//
////    private void continueV1(final Trace trace, final Map<String, String> mapOfDetectedProcessesInTrace,
////                            final Set<String> setOfDetectedTopicsInTrace) {
////        // Create SpanGraph from trace
////        final var spanGraph = (SpanGraph) trace.accept(traceToSpanGraphVisitor);
////        // Convert SpanGraph to BPMNChor
////        //spanGraph.accept(spanGraphToBPMNChorVisitor);
////        process(spanGraph, mapOfDetectedProcessesInTrace, setOfDetectedTopicsInTrace);
////    }
//
    private void continueV2(final Trace trace, final Map<String, String> mapOfDetectedProcessesInTrace,
                            final Set<String> setOfDetectedTopicsInTrace) {
        final var spanGuavaGraph = (ImmutableGraph<Span>) trace.accept(traceToSpanGuavaGraphVisitor);
        final var spanGraphToChoreographyGraphConverter = new SpanGraphToChoreographyGraphConverter(mapOfDetectedProcessesInTrace, setOfDetectedTopicsInTrace);
        // Print spanguavagraph edges
        System.out.println("PRINTING SPANGUAVAGRAPH EDGES");
        // edge.source().getOperationName() + " -> " + edge.target().getOperationName()
        spanGuavaGraph.edges().forEach(edge -> {
            final var message = MessageFormat.format("[{0} - {1}] --> [{2} - {3}]", edge.source().getOperationName(),
                    edge.source().getSpanID(), edge.target().getOperationName(), edge.target().getSpanID());
            System.out.println(message);
        });
        System.out.println("FINISHED PRINTING SPANGUAVAGRAPH EDGES");
        //spanGraphToChoreographyGraphConverter.convertSpanGraphToChoreographyGraph(spanGuavaGraph);

    }


    private void continueV3(final Trace trace, final Map<String, String> mapOfDetectedProcessesInTrace,
                            final Set<String> setOfDetectedTopicsInTrace) {
        TraceToSpanContainerGraphVisitor traceToSpanContainerGraphVisitor = new TraceToSpanContainerGraphVisitor();
        final var spanContainerGraph = (ImmutableGraph<SpanContainer>) trace.accept(traceToSpanContainerGraphVisitor);
        // Print spanContainerGraph
        // Print Edges
//        System.out.println("PRINTING SPANCONTAINERGRAPH EDGES");
//        spanContainerGraph.edges().forEach(System.out::println);
//        GuavaGraphMaximumDepthUtil.printNodesByDepth(spanContainerGraph);
        System.out.println("trace: " + trace);
        final var spanContainerGraphToChoreographyGraphConverter = new SpanContainerGraphToChoreographyGraphConverter();
        spanContainerGraphToChoreographyGraphConverter.convertSpanContainerGraphToChoreographyGraph(spanContainerGraph, mapOfDetectedProcessesInTrace);
    }
//
//    private void iterateOverSpanGraphAndPrintSpanIds(final ImmutableGraph<Span> spanGraph) {
//        final var spanNodes = spanGraph.nodes();
//        final var spanNodeIterator = spanNodes.iterator();
//        final var edges = spanGraph.edges();
//        edges.forEach(edge -> {
//            System.out.println(edge);
//        });
////        while (spanNodeIterator.hasNext()) {
////            final var spanNode = spanNodeIterator.next();
////            spanNode.
////        }
//    }
//
//    private void iterateOverGraphAndCreateChoreographyTasks(final ImmutableGraph<Span> spanGraph) {
//        final var spanNodes = spanGraph.nodes();
//        final var spanNodeIterator = spanNodes.iterator();
//        final var firstSpan = spanNodeIterator.next();
//        while (spanNodeIterator.hasNext()) {
//            final var span = spanNodeIterator.next();
//            final var childSpans = spanGraph.successors(span);
//
//
//        }
//    }
//
//
//    private void createFirstChoreographyTask(final ImmutableGraph<Span> spanGraph) {
//
//    }
//
//
////    private void process(final SpanGraph spanGraph, final Map<String, String> processesMap, final Set<String> topics) {
////        final var spanNodes = spanGraph.getSpanVertices();
////        // Create empty choreography
////        final var choreography = new Choreography(UUID.randomUUID().toString());
////        final var participantsSet = new HashSet<Participant>();
////        // Create first sequence flow
////        final var firstSequenceFlow = new SequenceFlow(UUID.randomUUID().toString());
////        // Create start event
////        final var startEvent = new StartEvent(UUID.randomUUID().toString());
////        choreography.setStartEvent(startEvent);
////        // Create a choreography task from first span
////        final var firstSpan = spanNodes.get(0).getSpan();
////        // By default, the first participant is always the user
////        final var firstParticipant = new Participant(UUID.randomUUID().toString(), "user");
////        final var firstChoreographyTask = ChoreographyTask.builder()
////                .id(UUID.randomUUID().toString())
////                .name(firstSpan.getOperationName())
////                .initiatingParticipantRef(firstParticipant.getId())
////                .build();
////        final var firstReceivingParticipant = new Participant(UUID.randomUUID().toString(),
////                processesMap.get(firstSpan.getProcessID()));
////    }
//
//    private void initiateChoreographyWithFirstSpan(final Choreography choreography, final SpanGraph spanGraph,
//                                                   final Map<String, String> processesMap) {
//        final var spanNodes = spanGraph.getSpanVertices();
//        // Create empty choreography
//        final var participantsSet = new HashSet<Participant>();
//        // Create first sequence flow
//        final var firstSequenceFlow = new SequenceFlow(UUID.randomUUID().toString());
//        // Create start event
//        final var startEvent = new StartEvent(UUID.randomUUID().toString(), firstSequenceFlow.getId());
//        choreography.setStartEvent(startEvent);
//        // Create a choreography task from first span
//        final var firstSpan = spanNodes.get(0).getSpan();
//        // By default, the first participant is always the user
//        final var firstParticipant = new Participant(UUID.randomUUID().toString(), "user");
//        final var firstChoreographyTask = ChoreographyTask.builder()
//                .id(UUID.randomUUID().toString())
//                .name(firstSpan.getOperationName())
//                .initiatingParticipantRef(firstParticipant.getId())
//                .build();
//        final var nameOfFirstMicroservice = processesMap.get(firstSpan.getProcessID());
//        final var firstReceivingParticipant = new Participant(UUID.randomUUID().toString(),
//                nameOfFirstMicroservice);
//        // Add first choreography task to choreography
//        choreography.addChoreographyTask(firstChoreographyTask);
//        // Add first receiving participant to choreography
//        choreography.addParticipant(firstReceivingParticipant);
//        // Add first participant to choreography
//        choreography.addParticipant(firstParticipant);
//        // Add first sequence flow to choreography
//        choreography.addSequenceFlow(firstSequenceFlow);
//        // Add first participant to participants set
//        participantsSet.add(firstParticipant);
//        // Add first receiving participant to participants set
//        participantsSet.add(firstReceivingParticipant);
//        // Create a sequence flow from start event to first choreography task
//        final var startEventToFirstChoreographyTaskSequenceFlow = new SequenceFlow(UUID.randomUUID().toString());
//        startEventToFirstChoreographyTaskSequenceFlow.setSourceRef(startEvent.getId());
//        startEventToFirstChoreographyTaskSequenceFlow.setTargetRef(firstChoreographyTask.getId());
//        choreography.addSequenceFlow(startEventToFirstChoreographyTaskSequenceFlow);
//    }
}

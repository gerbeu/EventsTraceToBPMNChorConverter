package com.example.eventstracetobpmnchorconverter.algorithm.converters;

import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.Message;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.bpmndiagram.BPMNDiagram;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.bpmndiagram.labelstyle.BPMNLabelStyle;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.bpmndiagram.labelstyle.Font;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.bpmndiagram.plane.BPMNPlane;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.bpmndiagram.plane.edge.BPMNEdge;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.bpmndiagram.plane.shape.BPMNShape;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.bpmndiagram.plane.shape.Bounds;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.bpmndiagram.plane.shape.ParticipantBandKind;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.ChoreographyShape;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.MessageFlow;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.choreographytask.ChoreographyTask;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.events.Event;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.gateway.Gateway;
import com.google.common.graph.Graph;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class ChoreographyGraphToBPMNDiagramConverter implements Converter<BPMNDiagram> {

    private final static int GATEWAY_HEIGHT = 50;
    private final static int GATEWAY_WIDTH = 50;
    private final static int EVENT_HEIGHT = 36;
    private final static int EVENT_WIDTH = 36;
    private final static int CHOREOGRAPHY_TASK_HEIGHT = 80;
    private final static int CHOREOGRAPHY_TASK_WIDTH = 100;
    private final static int PARTICIPANT_BAND_HEIGHT = 20;
    private final static int PARTICIPANT_BAND_WIDTH = 100;

    private final List<BPMNShape> shapes = new ArrayList<>();
    private final List<BPMNEdge> edges = new ArrayList<>();

    private final MutableGraph<ChoreographyShape> choreographyGraph;

    private final String choreographyId;

    private final Set<Message> messageHashSet;

    private final Map<Message, MessageFlow> messageMessageFlowMap;


    public ChoreographyGraphToBPMNDiagramConverter(Graph<ChoreographyShape> choreographyGraph,
                                                   final String choreographyId, Set<Message> messageHashSet, Map<Message, MessageFlow> messageMessageFlowMap) {
        this.choreographyGraph = Graphs.copyOf(choreographyGraph);
        this.choreographyId = choreographyId;
        this.messageHashSet = messageHashSet;
        this.messageMessageFlowMap = messageMessageFlowMap;
    }


    @Override
    public BPMNDiagram convert() {
        log.info("Converting choreography graph to BPMN diagram");
        choreographyGraph.nodes().forEach(choreographyShape -> {
            System.out.println("CONVERTING CHOREOGRAPHY SHAPE: " + choreographyShape.getId());
            createBPMNShape(choreographyShape);
        });
        final var bpmnPlane = new BPMNPlane(UUID.randomUUID().toString(), choreographyId, shapes, edges);
        final var bpmnLabelStyle = new BPMNLabelStyle(UUID.randomUUID().toString(), new Font("arial", "9"));
        return new BPMNDiagram(UUID.randomUUID().toString(), bpmnPlane, bpmnLabelStyle);
    }

    private void createBPMNShape(ChoreographyShape choreographyShape) {
        switch (choreographyShape) {
            case Event event -> createBPMNShapeForEvent(event);
            case Gateway gateway -> createBPMNShapeForGateway(gateway);
            case ChoreographyTask choreographyTask -> createBPMNShapeForChoreographyTask(choreographyTask);
            default -> throw new IllegalStateException("Unexpected value: " + choreographyShape);
        }
        ;
    }

    private void createBPMNShapeForEvent(final Event event) {
        shapes.add(BPMNShape.builder()
                .id(UUID.randomUUID().toString())
                .bpmnElement(event.getId())
                .bounds(createBoundsForEvent(event.getX(), event.getY()))
                .build());
    }

    private void createBPMNShapeForGateway(final Gateway gateway) {
        shapes.add(BPMNShape.builder()
                .id(UUID.randomUUID().toString())
                .bpmnElement(gateway.getId())
                .bounds(createBoundsForGateway(gateway.getX(), gateway.getY()))
                .build());
    }

    private void createBPMNShapeForChoreographyTask(final ChoreographyTask choreographyTask) {
        final var choreographyTaskBPMNShape = BPMNShape.builder()
                .id(UUID.randomUUID().toString())
                .bpmnElement(choreographyTask.getId())
                .bounds(createBoundsForChoreographyTask(choreographyTask.getX(), choreographyTask.getY()))
                .build();
        shapes.add(choreographyTaskBPMNShape);
        createBPMNShapeForTopInitiatingParticipantBand(choreographyTask, choreographyTaskBPMNShape);
        createBPMNShapeForBottomNonInitiatingParticipantBand(choreographyTask, choreographyTaskBPMNShape);
    }

    private void createBPMNShapeForTopInitiatingParticipantBand(final ChoreographyTask choreographyTask, final BPMNShape choreographyTaskBPMNShape) {
        final var messageFlowRef = choreographyTask.getMessageFlowRefs().stream().findFirst().orElseThrow();
        final var eventMessage = messageMessageFlowMap.entrySet().stream()
                .filter(entry -> entry.getValue().getId().equals(messageFlowRef))
                .map(Map.Entry::getKey)
                .findFirst().orElseThrow();
        final var isMessageVisible = !eventMessage.getName().equals("");
        shapes.add(BPMNShape.builder()
                .id(UUID.randomUUID().toString())
                .bpmnElement(choreographyTask.getInitiatingParticipantRef())
                .isHorizontal(String.valueOf(true))
                .isMessageVisible(String.valueOf(isMessageVisible))
                .participantBandKind(ParticipantBandKind.TOP_INITIATING.toString())
                .choreographyActivityShape(choreographyTaskBPMNShape.getId())
                .bounds(createBoundsForParticipantBand(choreographyTask.getX(), choreographyTask.getY()))
                .build());
    }

    private void createBPMNShapeForBottomNonInitiatingParticipantBand(final ChoreographyTask choreographyTask, final BPMNShape choreographyTaskBPMNShape) {
        shapes.add(BPMNShape.builder()
                .id(UUID.randomUUID().toString())
                .bpmnElement(choreographyTask.getParticipantRefs().stream()
                        .filter(participantRef -> !participantRef.equals(choreographyTask.getInitiatingParticipantRef()))
                        .findFirst()
                        .get())
                .isHorizontal(String.valueOf(true))
                .isMessageVisible(String.valueOf(false))
                .participantBandKind(ParticipantBandKind.BOTTOM_NON_INITIATING.toString())
                .choreographyActivityShape(choreographyTaskBPMNShape.getId())
                .bounds(createBoundsForParticipantBand(choreographyTask.getX(), choreographyTask.getY() + CHOREOGRAPHY_TASK_HEIGHT))
                .build());
    }

    private Bounds createBoundsForChoreographyTask(final int x, final int y) {
        return createBounds(x, y, CHOREOGRAPHY_TASK_WIDTH, CHOREOGRAPHY_TASK_HEIGHT);
    }

    private Bounds createBoundsForGateway(final int x, final int y) {
        return createBounds(x, y, GATEWAY_WIDTH, GATEWAY_HEIGHT);
    }

    private Bounds createBoundsForEvent(final int x, final int y) {
        return createBounds(x, y, EVENT_WIDTH, EVENT_HEIGHT);
    }

    private Bounds createBoundsForParticipantBand(final int x, final int y) {
        return createBounds(x, y, PARTICIPANT_BAND_WIDTH, PARTICIPANT_BAND_HEIGHT);
    }

    private Bounds createBounds(final int x, final int y, final int width, final int height) {
        return Bounds.builder()
                .x(String.valueOf(x))
                .y(String.valueOf(y))
                .width(String.valueOf(width))
                .height(String.valueOf(height))
                .build();
    }


}

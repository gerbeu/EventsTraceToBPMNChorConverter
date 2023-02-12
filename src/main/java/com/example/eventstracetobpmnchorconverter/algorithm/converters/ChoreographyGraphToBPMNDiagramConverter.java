package com.example.eventstracetobpmnchorconverter.algorithm.converters;

import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.bpmndiagram.BPMNDiagram;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.bpmndiagram.plane.shape.BPMNShape;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.ChoreographyShape;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.choreographytask.ChoreographyTask;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.events.Event;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.gateway.Gateway;
import com.google.common.graph.Graph;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;
import lombok.extern.slf4j.Slf4j;

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


    private final MutableGraph<ChoreographyShape> choreographyGraph;

    public ChoreographyGraphToBPMNDiagramConverter(Graph<ChoreographyShape> choreographyGraph) {
        System.out.println("CONVERTER CONSTRUCTOR");
        System.out.println(choreographyGraph.nodes().size());
        this.choreographyGraph = Graphs.copyOf(choreographyGraph);
        System.out.println(this.choreographyGraph.nodes().size());
    }


    @Override
    public BPMNDiagram convert() {
        log.info("Converting choreography graph to BPMN diagram");
        System.out.println(choreographyGraph.nodes().size());
        choreographyGraph.nodes().forEach(choreographyShape -> {
            System.out.println("CONVERTING CHOREOGRAPHY SHAPE: " + choreographyShape.getId());
        });
        choreographyGraph.nodes().stream().forEach(choreographyShape -> {
            if (choreographyShape instanceof ChoreographyTask) {
                System.out.println("CHOREOGRAPHY TASK: " + choreographyShape.getId());
            } else if (choreographyShape instanceof Gateway) {
                System.out.println("GATEWAY: " + choreographyShape.getId());
            } else if (choreographyShape instanceof Event) {
                System.out.println("EVENT: " + choreographyShape.getId());
            } else {
                System.out.println("UNKNOWN SHAPE: " + choreographyShape.getId());
            }
        });
        return null;
    }

    private BPMNShape createBPMNShape(ChoreographyShape choreographyShape) {
        return switch (choreographyShape) {
            case Event event -> createBPMNShapeForEvent(event);
            case Gateway gateway -> createBPMNShapeForGateway(gateway);
            case ChoreographyTask choreographyTask -> createBPMNShapeForChoreographyTask(choreographyTask);
            default -> throw new IllegalStateException("Unexpected value: " + choreographyShape);
        };

    }

    private BPMNShape createBPMNShapeForEvent(final Event event) {
//        return new BPMNShape(event.getId(), event.getName(),
//                event.getChoreographyActivityShape().getBounds().getX(),
//                event.getChoreographyActivityShape().getBounds().getY(),
//                EVENT_WIDTH, EVENT_HEIGHT);
        return null;
    }

    private BPMNShape createBPMNShapeForGateway(final Gateway gateway) {
        return null;
    }

    private BPMNShape createBPMNShapeForChoreographyTask(ChoreographyTask choreographyTask) {
        return null;
    }

}

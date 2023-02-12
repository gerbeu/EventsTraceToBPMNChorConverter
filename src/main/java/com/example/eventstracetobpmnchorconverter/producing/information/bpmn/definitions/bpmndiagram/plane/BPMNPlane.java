package com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.bpmndiagram.plane;

import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.bpmndiagram.plane.edge.BPMNEdge;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.bpmndiagram.plane.shape.BPMNShape;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class BPMNPlane {

    @JacksonXmlProperty(isAttribute = true)
    private final String id;

    @JacksonXmlProperty(isAttribute = true)
    private final String bpmnElement;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "bpmndi:BPMNShape")
    private List<BPMNShape> shapes;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "bpmndi:BPMNEdge")
    private List<BPMNEdge> edges;

    public void addShape(BPMNShape shape) {
        shapes.add(shape);
    }

    public void addEdge(BPMNEdge edge) {
        edges.add(edge);
    }

}

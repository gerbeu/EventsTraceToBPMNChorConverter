package com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.bpmndiagram;

import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.bpmndiagram.labelstyle.BPMNLabelStyle;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.bpmndiagram.plane.BPMNPlane;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@JacksonXmlRootElement(localName = "bpmndi:BPMNDiagram")
public class BPMNDiagram {

    @JacksonXmlProperty(isAttribute = true)
    private final String id;

    @JacksonXmlProperty(localName = "bpmndi:BPMNPlane")
    private BPMNPlane bpmnPlane;
    @JacksonXmlProperty(localName = "bpmndi:BPMNLabelStyle")
    private BPMNLabelStyle bpmnLabelStyle;

}

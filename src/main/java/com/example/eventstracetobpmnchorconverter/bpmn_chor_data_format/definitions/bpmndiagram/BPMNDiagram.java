package com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.bpmndiagram;

import com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.bpmndiagram.labelstyle.BPMNLabelStyle;
import com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.bpmndiagram.plane.BPMNPlane;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BPMNDiagram {

    @JacksonXmlProperty(isAttribute = true)
    private final String id;

    @JacksonXmlProperty(localName = "bpmndi:BPMNPlane")
    private BPMNPlane bpmnPlane;
    @JacksonXmlProperty(localName = "bpmndi:BPMNLabelStyle")
    private BPMNLabelStyle bpmnLabelStyle;

}

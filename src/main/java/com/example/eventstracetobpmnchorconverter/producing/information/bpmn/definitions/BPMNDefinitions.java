package com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions;

import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.bpmndiagram.BPMNDiagram;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.Choreography;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@JacksonXmlRootElement(localName = "bpmn2:definitions")
@AllArgsConstructor
@Getter
@Builder
public class BPMNDefinitions {

    // TODO xmlns...

    @JacksonXmlProperty(isAttribute = true)
    private String id;

    @JacksonXmlProperty(isAttribute = true)
    private String targetNamespace;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "bpmn2:message")
    private List<Message> messages;

    @JacksonXmlProperty(localName = "bpmn2:choreography", isAttribute = false)
    private Choreography choreography;

    @JacksonXmlProperty(localName = "bpmndi:BPMNDiagram", isAttribute = false)
    private BPMNDiagram bpmnDiagram;

}

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
public class BPMNDefinitions {

    @JacksonXmlProperty(localName = "xmlns:xsi", isAttribute = true)
    private String xmlnsXsi;

    @JacksonXmlProperty(localName = "xmlns:bpmn2", isAttribute = true)
    private String xmlnsBpmn2 = "http://www.omg.org/spec/BPMN/20100524/MODEL";

    @JacksonXmlProperty(localName = "xmlns:bpmndi", isAttribute = true)
    private String xmlnsBpmndi = "http://www.omg.org/spec/BPMN/20100524/DI";

    @JacksonXmlProperty(localName = "xmlns:dc", isAttribute = true)
    private String xmlnsDc = "http://www.omg.org/spec/DD/20100524/DC";

    @JacksonXmlProperty(localName = "xmlns:di", isAttribute = true)
    private String xmlnsDi = "http://www.omg.org/spec/DD/20100524/DI";

    @JacksonXmlProperty(localName = "xmlns:xs", isAttribute = true)
    private String xmlnsXs = "http://www.w3.org/2001/XMLSchema";

    @JacksonXmlProperty(localName = "targetNamespace" ,isAttribute = true)
    private String targetNamespace = "http://bpmn.io/schema/bpmn";

    @JacksonXmlProperty(isAttribute = true)
    private String id;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "bpmn2:message")
    private List<Message> messages;

    @JacksonXmlProperty(localName = "bpmn2:choreography", isAttribute = false)
    private Choreography choreography;

    @JacksonXmlProperty(localName = "bpmndi:BPMNDiagram", isAttribute = false)
    private BPMNDiagram bpmnDiagram;

    public BPMNDefinitions(String id, List<Message> messages, Choreography choreography, BPMNDiagram bpmnDiagram) {
        this.id = id;
        this.messages = messages;
        this.choreography = choreography;
        this.bpmnDiagram = bpmnDiagram;
        this.xmlnsXsi = "http://www.w3.org/2001/XMLSchema-instance";
        this.xmlnsXs = "http://www.w3.org/2001/XMLSchema";
        this.targetNamespace = "http://bpmn.io/schema/bpmn";
        this.xmlnsBpmn2 = "http://www.omg.org/spec/BPMN/20100524/MODEL";
        this.xmlnsBpmndi = "http://www.omg.org/spec/BPMN/20100524/DI";
        this.xmlnsDc = "http://www.omg.org/spec/DD/20100524/DC";
        this.xmlnsDi = "http://www.omg.org/spec/DD/20100524/DI";
    }
}

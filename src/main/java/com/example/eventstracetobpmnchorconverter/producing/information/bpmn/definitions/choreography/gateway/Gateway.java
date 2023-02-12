package com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.gateway;

import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.ChoreographyShape;
import com.example.eventstracetobpmnchorconverter.util.RandomIDGenerator;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class Gateway extends ChoreographyShape {

    @JacksonXmlProperty(isAttribute = true)
    private final String id;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "bpmn2:incoming")
    private List<String> incomingFlows;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "bpmn2:outgoing")
    private List<String> outgoingFlows;

    protected Gateway(String id) {
        this.id = id == null ? RandomIDGenerator.generateWithPrefix("Gateway") : id;
        incomingFlows = new ArrayList<>();
        outgoingFlows = new ArrayList<>();
    }

    public void addIncomingFlow(String incomingFlow) {
        incomingFlows.add(incomingFlow);
    }

    public void addOutgoingFlow(String outgoingFlow) {
        outgoingFlows.add(outgoingFlow);
    }

}

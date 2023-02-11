package com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.choreography.gateway;

import com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.choreography.ChoreographyShape;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public abstract class Gateway extends ChoreographyShape {

    private final String id;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "bpmn2:incoming")
    private List<String> incomingFlows;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "bpmn2:outgoing")
    private List<String> outgoingFlows;

    protected Gateway(String id, List<String> incomingFlows, List<String> outgoingFlows) {
        this.id = id == null ? UUID.randomUUID().toString() : id;
        this.incomingFlows = incomingFlows;
        this.outgoingFlows = outgoingFlows;
    }
}

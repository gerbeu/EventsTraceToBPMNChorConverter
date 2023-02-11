package com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.choreography.events;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;

@Getter
public class StartEvent extends Event {

    // "bpmn2:startEvent"

    @JacksonXmlProperty(isAttribute = true)
    private final String id;

    @JacksonXmlProperty(isAttribute = false, localName = "bpmn2:outgoing")
    private String outgoingFlow;


    public StartEvent(String id) {
        this.id = id;
    }

    public void setOutgoingFlow(String outgoingFlow) {
        this.outgoingFlow = outgoingFlow;
    }

}

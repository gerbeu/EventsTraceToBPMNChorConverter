package com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.choreography.events;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class EndEvent extends Event {

    @JacksonXmlProperty(isAttribute = true)
    private final String id;


    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "bpmn2:incoming")
    private List<String> incomingFlows;

}

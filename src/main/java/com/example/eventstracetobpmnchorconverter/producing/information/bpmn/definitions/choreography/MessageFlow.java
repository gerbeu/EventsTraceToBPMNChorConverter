package com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@JacksonXmlRootElement(localName = "bpmn2:messageFlow")
public class MessageFlow {

    // name = "bpmn2:messageFlow"

    @JacksonXmlProperty(isAttribute = true)
    private final String id;
    @JacksonXmlProperty(isAttribute = true)
    private final String sourceRef;
    @JacksonXmlProperty(isAttribute = true)
    private final String targetRef;
    @JacksonXmlProperty(isAttribute = true)
    private final String messageRef;


}

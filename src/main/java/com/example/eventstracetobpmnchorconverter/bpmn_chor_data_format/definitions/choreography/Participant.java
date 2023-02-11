package com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.choreography;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class Participant {

    // "bpmn2:participant"

    @JacksonXmlProperty(isAttribute = true)
    private final String id;
    @JacksonXmlProperty(isAttribute = true)
    private final String name;

}


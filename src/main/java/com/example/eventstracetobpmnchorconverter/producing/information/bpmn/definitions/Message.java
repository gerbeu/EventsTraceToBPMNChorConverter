package com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Message {

    @JacksonXmlProperty(isAttribute = true)
    private final String id;
    @JacksonXmlProperty(isAttribute = true)
    private final String name;
}

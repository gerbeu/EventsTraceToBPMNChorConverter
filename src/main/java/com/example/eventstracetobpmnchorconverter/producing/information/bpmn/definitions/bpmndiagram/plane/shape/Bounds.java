package com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.bpmndiagram.plane.shape;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class Bounds {

    @JacksonXmlProperty(isAttribute = true)
    private final String x;

    @JacksonXmlProperty(isAttribute = true)
    private final String y;

    @JacksonXmlProperty(isAttribute = true)
    private final String width;

    @JacksonXmlProperty(isAttribute = true)
    private final String height;

}

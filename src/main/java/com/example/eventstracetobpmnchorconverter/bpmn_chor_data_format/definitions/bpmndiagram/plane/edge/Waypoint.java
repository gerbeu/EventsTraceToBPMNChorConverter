package com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.bpmndiagram.plane.edge;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class Waypoint {

    @JacksonXmlProperty(isAttribute = true)
    private final String x;

    @JacksonXmlProperty(isAttribute = true)
    private final String y;

}

package com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.bpmndiagram.plane.shape;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class BPMNShape {

    @JacksonXmlProperty(isAttribute = true)
    private final String id;

    @JacksonXmlProperty(isAttribute = true)
    private final String bpmnElement;

    // optional
    @JacksonXmlProperty(isAttribute = true)
    private final String isMessageVisible;

    @JacksonXmlProperty(isAttribute = true)
    private final String isHorizontal;

    @JacksonXmlProperty(isAttribute = true)
    private final String participantBandKind;

    @JacksonXmlProperty(isAttribute = true)
    private final String choreographyActivityShape;

    @JacksonXmlProperty(localName = "dc:Bounds")
    private final Bounds bounds;

}

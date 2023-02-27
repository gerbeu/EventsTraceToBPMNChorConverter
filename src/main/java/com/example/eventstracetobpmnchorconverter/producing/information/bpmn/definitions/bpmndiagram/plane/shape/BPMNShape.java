package com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.bpmndiagram.plane.shape;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Builder;
import lombok.Getter;

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

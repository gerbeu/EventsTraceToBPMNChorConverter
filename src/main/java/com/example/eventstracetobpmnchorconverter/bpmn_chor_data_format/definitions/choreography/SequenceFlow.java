package com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.choreography;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;

import java.util.UUID;

@Getter
public class SequenceFlow extends ChoreographyShape {

    @JacksonXmlProperty(isAttribute = true)
    private final String id;

    @JacksonXmlProperty(isAttribute = true)
    private String sourceRef;

    @JacksonXmlProperty(isAttribute = true)
    private String targetRef;

    public SequenceFlow(String id) {
        this.id = id == null ? UUID.randomUUID().toString() : id;
    }

    public void setSourceRef(String sourceRef) {
        this.sourceRef = sourceRef;
    }

    public void setTargetRef(String targetRef) {
        this.targetRef = targetRef;
    }

}

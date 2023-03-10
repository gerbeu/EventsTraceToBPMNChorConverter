package com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography;

import com.example.eventstracetobpmnchorconverter.util.RandomIDGenerator;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SequenceFlow extends ChoreographyEdge { // War vorher ChoreographyShape

    @JacksonXmlProperty(isAttribute = true)
    private final String id;

    @JacksonXmlProperty(isAttribute = true)
    private String sourceRef;

    @JacksonXmlProperty(isAttribute = true)
    private String targetRef;

    public SequenceFlow(String id) {
        this.id = id == null ? RandomIDGenerator.generateWithPrefix("SequenceFlow") : id;
    }

    public void setSourceRef(String sourceRef) {
        this.sourceRef = sourceRef;
    }

    public void setTargetRef(String targetRef) {
        this.targetRef = targetRef;
    }

}

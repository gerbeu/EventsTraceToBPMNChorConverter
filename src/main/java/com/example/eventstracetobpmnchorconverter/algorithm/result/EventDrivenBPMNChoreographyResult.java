package com.example.eventstracetobpmnchorconverter.algorithm.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EventDrivenBPMNChoreographyResult extends Result {

    @JsonProperty
    private final String bpmnDefinitionsXml;
}

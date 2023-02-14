package com.example.eventstracetobpmnchorconverter.algorithm.result;

import com.example.eventstracetobpmnchorconverter.algorithm.topicsEventsInfo.Topic;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

import java.util.Set;

@AllArgsConstructor
public class EventDrivenBPMNChoreographyResult extends Result {

    @JsonProperty
    private final String bpmnDefinitionsXml;

    @JsonProperty
    private final Set<Topic> topicsEventsInfo;
}

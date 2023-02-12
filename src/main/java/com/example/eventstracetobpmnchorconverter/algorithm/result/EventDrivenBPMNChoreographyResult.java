package com.example.eventstracetobpmnchorconverter.algorithm.result;

import com.example.eventstracetobpmnchorconverter.producing.information.EventsInfo;
import com.example.eventstracetobpmnchorconverter.producing.information.TopicsInfo;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.BPMNDefinitions;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EventDrivenBPMNChoreographyResult extends Result {

    private final EventsInfo eventsInfo;

    private final TopicsInfo topicsInfo;

    @JsonProperty
    private final String bpmnDefinitionsXml;
}

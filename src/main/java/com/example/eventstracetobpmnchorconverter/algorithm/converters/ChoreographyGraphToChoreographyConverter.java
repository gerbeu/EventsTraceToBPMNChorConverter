package com.example.eventstracetobpmnchorconverter.algorithm.converters;

import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.Choreography;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChoreographyGraphToChoreographyConverter implements Converter<Choreography> {

    @Override
    public Choreography convert() {
        log.info("Converting choreography graph to choreography");
        return null;
    }
}

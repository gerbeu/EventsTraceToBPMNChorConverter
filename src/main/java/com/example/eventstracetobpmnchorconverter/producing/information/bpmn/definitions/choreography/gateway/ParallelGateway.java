package com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.gateway;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "bpmn2:parallelGateway")
public class ParallelGateway extends Gateway {

    public ParallelGateway(String id) {
        super(id);
    }

}

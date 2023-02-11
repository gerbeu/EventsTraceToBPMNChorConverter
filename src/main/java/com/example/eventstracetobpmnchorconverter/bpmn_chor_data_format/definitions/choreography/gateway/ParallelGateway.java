package com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.choreography.gateway;

import java.util.List;

public class ParallelGateway extends Gateway {

    protected ParallelGateway(String id, List<String> incomingFlows, List<String> outgoingFlows) {
        super(id, incomingFlows, outgoingFlows);
    }

}

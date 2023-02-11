package com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.choreography.choreographytask;

import com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.choreography.ChoreographyShape;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class ChoreographyTask extends ChoreographyShape {

    @JacksonXmlProperty(isAttribute = true)
    private final String id;

    @JacksonXmlProperty(isAttribute = true)
    private final String name;

    // TODO: check bpmn name
    @JacksonXmlProperty(isAttribute = true)
    private final String initiatingParticipantRef;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "bpmn2:participantRef")
    private final List<String> participantRefs;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "bpmn2:messageFlowRef")
    private final List<String> messageFlowRefs;

    @JacksonXmlProperty(localName = "bpmn2:incoming")
    private String incomingFlow;

    @JacksonXmlProperty(localName = "bpmn2:outgoing")
    private String outgoingFlow;

    public ChoreographyTask(String id, String name, String initiatingParticipantRef, List<String> participantRefs, List<String> messageFlowRefs) {
        this.id = id;
        this.name = name;
        this.initiatingParticipantRef = initiatingParticipantRef;
        this.participantRefs = participantRefs;
        this.messageFlowRefs = messageFlowRefs;
    }

    public void setIncomingFlow(String incomingFlow) {
        this.incomingFlow = incomingFlow;
    }

    public void setOutgoingFlow(String outgoingFlow) {
        this.outgoingFlow = outgoingFlow;
    }

    // to string


    @Override
    public String toString() {
        return "ChoreographyTask{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
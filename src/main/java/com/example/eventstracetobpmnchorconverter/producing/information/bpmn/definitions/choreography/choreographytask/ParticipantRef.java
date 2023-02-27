package com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.choreographytask;

import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.Participant;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;

@Getter
@JacksonXmlRootElement(localName = "bpmn2:participantRef")
public class ParticipantRef {

    private final String participantID;

    public ParticipantRef(final Participant participant) {
        this.participantID = participant.getId();
    }
}

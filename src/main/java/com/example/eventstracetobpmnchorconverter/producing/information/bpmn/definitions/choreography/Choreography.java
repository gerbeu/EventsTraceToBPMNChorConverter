package com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography;

import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.choreographytask.ChoreographyTask;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.events.EndEvent;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.events.StartEvent;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.gateway.ParallelGateway;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
@JacksonXmlRootElement(localName = "bpmn2:choreography")
public class Choreography {

    @JacksonXmlProperty(isAttribute = true)
    private final String id;

    @JacksonXmlProperty(isAttribute = true)
    private final String name;

    @JacksonXmlProperty(localName = "bpmn2:startEvent")
    private StartEvent startEvent;

    // Actually, there are more than one
    @JacksonXmlProperty(localName = "bpmn2:endEvent")
    private EndEvent endEvent;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "bpmn2:participant")
    private List<Participant> participants;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "bpmn2:messageFlow")
    private List<MessageFlow> messageFlows;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "bpmn2:choreographyTask")
    private List<ChoreographyTask> choreographyTasks;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "bpmn2:sequenceFlow")
    private List<SequenceFlow> sequenceFlows;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "bpmn2:parallelGateway")
    private List<ParallelGateway> gateways;


    public void addChoreographyTask(ChoreographyTask choreographyTask) {
        choreographyTasks.add(choreographyTask);
    }

    public void addSequenceFlow(SequenceFlow sequenceFlow) {
        sequenceFlows.add(sequenceFlow);
    }

    public void addMessageFlow(MessageFlow messageFlow) {
        messageFlows.add(messageFlow);
    }

    public void addParticipant(Participant participant) {
        participants.add(participant);
    }

    public void setStartEvent(StartEvent startEvent) {
        this.startEvent = startEvent;
    }

    public void setEndEvent(EndEvent endEvent) {
        this.endEvent = endEvent;
    }
}

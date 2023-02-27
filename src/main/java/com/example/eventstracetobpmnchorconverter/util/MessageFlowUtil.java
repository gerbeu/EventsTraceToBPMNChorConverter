package com.example.eventstracetobpmnchorconverter.util;

import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.Message;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.MessageFlow;
import com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography.Participant;

import java.util.Map;

public class MessageFlowUtil {

    public static MessageFlow createMessageFlow(Participant initiatingParticipant, Participant receivingParticipant,
                                                 Message message, Map<Message, MessageFlow> messageMessageFlowMap) {
        final var messageFlow = MessageFlow.builder()
                .id(RandomIDGenerator.generateWithPrefix("MessageFlow"))
                .sourceRef(initiatingParticipant.getId())
                .targetRef(receivingParticipant.getId())
                .messageRef(message.getId())
                .build();
        messageMessageFlowMap.put(message, messageFlow);
        return messageFlow;
    }
}

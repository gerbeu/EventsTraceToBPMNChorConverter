package com.example.eventstracetobpmnchorconverter.util;

import com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.Message;
import com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.choreography.MessageFlow;
import com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.choreography.Participant;

import java.util.Map;
import java.util.UUID;

public class MessageFlowUtil {

    public static MessageFlow createMessageFlow(Participant initiatingParticipant, Participant receivingParticipant,
                                                 Message message, Map<Message, MessageFlow> messageMessageFlowMap) {
        final var messageFlow = new MessageFlow(
                UUID.randomUUID().toString(),
                initiatingParticipant.getId(),
                receivingParticipant.getId(),
                message.getId()
        );
        messageMessageFlowMap.put(message, messageFlow);
        return messageFlow;
    }
}

package com.example.eventstracetobpmnchorconverter.producing;

import com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.choreography.Participant;
import com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.choreography.choreographytask.ChoreographyTask;
import com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.choreography.choreographytask.ParticipantRef;
import com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.choreography.events.EndEvent;
import com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.choreography.events.StartEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.util.List;

public class XMLTest {

    void main() throws JsonProcessingException {
//        StartEvent startEvent = new StartEvent("Startevent_1", "");
//        EndEvent endEvent = new EndEvent("Endevent_1", List.of(""));
//
//        Participant participant1 = new Participant("Participant_1", "Max");
//        Participant participant2 = new Participant("Participant_2", "Thomas");
//
//        ParticipantRef participantRef1 = new ParticipantRef(participant1);
//        ParticipantRef participantRef2 = new ParticipantRef(participant2);
//
//
//        final var choreographyTask1 = ChoreographyTask.builder()
//                .name("hand over pizza")
//                .initiatingParticipantRef(participant1.getId())
//                .participantRefs(List.of(participant1.getId(), participant2.getId()))
//                .build();
//
//        XmlMapper xmlMapper = new XmlMapper();
//        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
//        String xml = xmlMapper.writeValueAsString(choreographyTask1);
//        System.out.println(xml);




//        SequenceFlow sequenceFlow1 = SequenceFlow.builder()
//                .id("SequenceFlow_1")
//                .sourceRef(startEvent.getId())
//                .targetRef()
//
//
//
//        Message message1 = new Message("Message_1", "");
//
//
//        final var choreography = Choreography.builder()
//                .id("test-choreography")
//                .participants(List.of(participant1, participant2))
//                .startEvent(startEvent)
//                .sequenceFlows(List.of())
//                .build();
//
//
//
//        final var bpmnDefinitions = BPMNDefinitions.builder()
//                .choreography()
//                .bpmnDiagram()
//                .build();
//
//        final var definitions = new BPMNDefinitions("_tTv5YOycEeiHGOQ2NkJZNQ", "http://bpmn.io/schema/bpmn", new Choreography(
//                "Choreography"),
//                new BPMNDiagram("BPMNDiagram_1", new BPMNPlane("BPMNPlane_Choreography_1", "Choreography"),
//                        new BPMNLabelStyle("BPMNLabelStyle_1", new Font("arial", "9.0"))));
//        XmlMapper xmlMapper = new XmlMapper();
//        String xml = xmlMapper.writeValueAsString(definitions);
//        System.out.println(xml);


//        final var participant = new Participant("1", "max");
//
//        XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
//        XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newFactory();
//        StringWriter out = new StringWriter();
//        XMLStreamWriter sw = xmlOutputFactory.createXMLStreamWriter(out);
//
//        XmlMapper mapper = new XmlMapper(xmlInputFactory);
//
//        sw.writeStartDocument();
//        sw.writeStartElement("root");
//
//        mapper.writeValue(sw, participant);
//        sw.writeComment("Some insightful commentary here");
//        sw.writeEndElement();
//        sw.writeEndDocument();
//
//

//        ObjectMapper xmlMapper = new XmlMapper();
//        try {
//            final var xml = xmlMapper.writeValueAsString(participant);
//            System.out.println(xml);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
    }
}

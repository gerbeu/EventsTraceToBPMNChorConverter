package com.example.eventstracetobpmnchorconverter.bpmn_chor_data_format.definitions.bpmndiagram.labelstyle;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class BPMNLabelStyle {

    @JacksonXmlProperty(isAttribute = true)
    private final String id;

    @JacksonXmlProperty(localName = "dc:Font")
    private final Font font;

}

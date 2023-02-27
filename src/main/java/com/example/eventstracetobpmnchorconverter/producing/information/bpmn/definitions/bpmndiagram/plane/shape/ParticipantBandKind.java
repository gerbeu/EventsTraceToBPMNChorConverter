package com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.bpmndiagram.plane.shape;

public enum ParticipantBandKind {
    TOP_INITIATING("top_initiating"),
    TOP_NON_INITIATING("top_non_initiating"),
    BOTTOM_INITIATING("bottom_initiating"),
    BOTTOM_NON_INITIATING("bottom_non_initiating");

    private final String value;

    private ParticipantBandKind(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

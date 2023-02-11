package com.example.eventstracetobpmnchorconverter.producing.graph;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SpanContainer {

    private final String id;
    private final SpanComponent spanComponent;

    public SpanContainer(String id, SpanComponent spanComponent) {
        this.id = id;
        this.spanComponent = spanComponent;
    }
}

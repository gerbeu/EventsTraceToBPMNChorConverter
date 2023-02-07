package com.example.eventstracetobpmnchorconverter.consume.jaegerTrace.spans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Getter
public class Reference {

    private String refType;
    private String traceID;
    private String spanID;

}

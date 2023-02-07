package com.example.eventstracetobpmnchorconverter.consume.jaegerTrace.spans;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Getter
public class Tag {

    private String key;
    private String type;
    private String value;

}

package com.example.eventstracetobpmnchorconverter.producing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Event {

    @JsonProperty("type")
    private final String type;

    @JsonCreator
    public Event(@JsonProperty("type") String type) {
        this.type = type;
    }
}

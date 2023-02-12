package com.example.eventstracetobpmnchorconverter.producing.information.bpmn.definitions.choreography;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ChoreographyShape {

    @JsonIgnore
    protected int x;
    @JsonIgnore
    protected int y;

    public String getId() {
        return null;
    }


}

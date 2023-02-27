package com.example.eventstracetobpmnchorconverter.jaegerTrace.processes;

import com.example.eventstracetobpmnchorconverter.jaegerTrace.spans.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class Process {
    private String serviceName;
    private List<Tag> tags;
}

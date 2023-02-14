package com.example.eventstracetobpmnchorconverter.algorithm.services;

import com.example.eventstracetobpmnchorconverter.algorithm.visitors.jaeger_trace.ProcessesInfoVisitor;
import com.example.eventstracetobpmnchorconverter.jaegerTrace.Trace;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Getter
public class MicroservicesInfoService {

    private Map<String, String> processMicroserviceMap = new HashMap<>();

    public void createMicroservicesInfo(final Trace trace) {
        processMicroserviceMap = (Map<String, String>) trace.accept(new ProcessesInfoVisitor());
    }

    public String getMicroserviceNameByProcessId(final String processId) {
        return processMicroserviceMap.get(processId);
    }


}

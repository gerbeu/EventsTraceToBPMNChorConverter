package com.example.eventstracetobpmnchorconverter.jaegerTrace.criteria;

import com.example.eventstracetobpmnchorconverter.jaegerTrace.Trace;
import com.example.eventstracetobpmnchorconverter.jaegerTrace.processes.Process;

import java.util.ArrayList;
import java.util.List;

public class ProcessTagCriteria implements Criteria {

    private static final List<String> allowedTagKeys = List.of();

    private static boolean isAllowedTagKey(String tagKey) {
        return allowedTagKeys.contains(tagKey);
    }

    @Override
    public void meetCriteria(Trace trace) {
        final var filteredProcesses = new ArrayList<Process>();
        for (final Process process : trace.getProcesses().values()) {
            final var filteredTags = process.getTags().stream()
                    .filter(tag -> isAllowedTagKey(tag.getKey()))
                    .toList();
            process.setTags(filteredTags);
            filteredProcesses.add(process);
        }
    }
}

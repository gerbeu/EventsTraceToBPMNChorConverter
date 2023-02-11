package com.example.eventstracetobpmnchorconverter.jaegerTrace.criteria;

import com.example.eventstracetobpmnchorconverter.jaegerTrace.Trace;
import com.example.eventstracetobpmnchorconverter.jaegerTrace.spans.Span;
import com.example.eventstracetobpmnchorconverter.jaegerTrace.spans.Tag;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
public class TagCriteria implements Criteria {

    private static final String MESSAGING_SYSTEM = "messaging.system";
    private static final String MESSAGING_DESTINATION = "messaging.destination";
    private static final String MESSAGING_DESTINATION_KIND = "messaging.destination_kind";
    private static final String PRODUCED_EVENT = "produced.event";
    private static final String PROCESSED_EVENT = "processed.event";

    private static final List<String> allowedTagKeys = List.of(MESSAGING_SYSTEM, MESSAGING_DESTINATION,
            MESSAGING_DESTINATION_KIND,
            PRODUCED_EVENT, PROCESSED_EVENT);

    private static boolean isAllowedTagKey(String tagKey) {
        return allowedTagKeys.contains(tagKey);
    }

    @Override
    public void meetCriteria(Trace trace) {
        final List<Span> spans = trace.getSpans();
        final Span firstSpan = spans.get(0);
        final List<Span> filteredSpans = new ArrayList<>(spans.stream()
                .skip(1)
                .map(span -> {
                    final var filteredTags = Arrays.stream(span.getTags())
                            .filter(tag -> isAllowedTagKey(tag.getKey()))
                            .toArray(Tag[]::new);
                    span.setTags(filteredTags);
                    return span;
                })
                .filter(span -> Arrays.stream(span.getTags()).anyMatch(tag -> isAllowedTagKey(tag.getKey())))
                .toList());
        filteredSpans.add(0, firstSpan);
        trace.setSpans(filteredSpans);
    }


}

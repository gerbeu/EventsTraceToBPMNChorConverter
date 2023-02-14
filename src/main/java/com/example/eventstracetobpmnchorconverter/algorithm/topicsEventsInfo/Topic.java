package com.example.eventstracetobpmnchorconverter.algorithm.topicsEventsInfo;

import lombok.Getter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Getter
@ToString
public class Topic {
    private String topicId;
    private String topicName;
    private Set<String> consumers;
    private Set<String> producers;
    private Set<String> events;

    public Topic(String topicId, String topicName) {
        this.topicId = topicId;
        this.topicName = topicName;
        consumers = new HashSet<>();
        producers = new HashSet<>();
        events = new HashSet<>();
    }

    public void addConsumer(String consumer) {
        consumers.add(consumer);
    }

    public void addProducer(String producer) {
        producers.add(producer);
    }

    public void addEvent(String event) {
        events.add(event);
    }


}






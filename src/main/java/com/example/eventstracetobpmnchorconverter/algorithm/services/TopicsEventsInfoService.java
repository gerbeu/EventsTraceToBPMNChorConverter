package com.example.eventstracetobpmnchorconverter.algorithm.services;

import com.example.eventstracetobpmnchorconverter.algorithm.topicsEventsInfo.Topic;
import com.example.eventstracetobpmnchorconverter.util.RandomIDGenerator;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.*;

@Service
@Getter
@RequestScope
public class TopicsEventsInfoService {

    private Set<Topic> topics = new HashSet<>();


    public void addEventToTopic(final String eventValue, final String topicName) {
        // get topic from topics with name topicname
        final var existsTopicWithTopicName = topics.stream()
                .anyMatch(t -> t.getTopicName().equals(topicName));
        if (!existsTopicWithTopicName) {
            final var topic = new Topic(RandomIDGenerator.generateWithPrefix("Topic"), topicName);
            topic.addEvent(eventValue);
            topics.add(topic);
        } else {
            final var topic = topics.stream()
                    .filter(t -> t.getTopicName().equals(topicName))
                    .findFirst()
                    .orElse(null);
            topic.addEvent(eventValue);
        }
    }

    public void addProducerToTopic(final String producerName, final String topicName) {
        // get topic from topics with name topicname
        final var existsTopicWithTopicName = topics.stream()
                .anyMatch(t -> t.getTopicName().equals(topicName));
        if (!existsTopicWithTopicName) {
            final var topic = new Topic(RandomIDGenerator.generateWithPrefix("Topic"), topicName);
            topic.addProducer(producerName);
            topics.add(topic);
        } else {
            final var topic = topics.stream()
                    .filter(t -> t.getTopicName().equals(topicName))
                    .findFirst()
                    .orElse(null);
            topic.addProducer(producerName);
        }
    }

    public void addConsumerToTopic(final String consumerName, final String topicName) {
        // get topic from topics with name topicname
        final var existsTopicWithTopicName = topics.stream()
                .anyMatch(t -> t.getTopicName().equals(topicName));
        if (!existsTopicWithTopicName) {
            final var topic = new Topic(RandomIDGenerator.generateWithPrefix("Topic"), topicName);
            topic.addConsumer(consumerName);
            topics.add(topic);
        } else {
            final var topic = topics.stream()
                    .filter(t -> t.getTopicName().equals(topicName))
                    .findFirst()
                    .orElse(null);
            topic.addConsumer(consumerName);
        }
    }

}

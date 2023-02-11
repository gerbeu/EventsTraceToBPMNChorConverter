package com.example.eventstracetobpmnchorconverter.tests;

import com.example.eventstracetobpmnchorconverter.producing.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EventTest {

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Event eventType = mapper.readValue("{ \"type\": \"OrderCreated\", \"version\" : \"1.0.0\", }", Event.class);
    }
}


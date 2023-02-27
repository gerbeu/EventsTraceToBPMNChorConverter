package com.example.eventstracetobpmnchorconverter.controllers;

import com.example.eventstracetobpmnchorconverter.algorithm.EventDrivenBPMNChoreographyAlgorithm;
import com.example.eventstracetobpmnchorconverter.algorithm.result.EventDrivenBPMNChoreographyResult;
import com.example.eventstracetobpmnchorconverter.jaegerTrace.Trace;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
public class JaegerUIController {

    @Autowired
    private final EventDrivenBPMNChoreographyAlgorithm eventDrivenBPMNChoreographyAlgorithm;

    public JaegerUIController(EventDrivenBPMNChoreographyAlgorithm eventDrivenBPMNChoreographyAlgorithm) {
        this.eventDrivenBPMNChoreographyAlgorithm = eventDrivenBPMNChoreographyAlgorithm;
    }

    @PostMapping("/algorithm")
    @ResponseBody()
    public ResponseEntity<EventDrivenBPMNChoreographyResult> processAlgorithmRequest(@RequestBody Trace trace) {
        log.info("In processAlgorithmRequest");
        final var result = eventDrivenBPMNChoreographyAlgorithm.run(trace);
        return ResponseEntity.ok(result);
    }

}
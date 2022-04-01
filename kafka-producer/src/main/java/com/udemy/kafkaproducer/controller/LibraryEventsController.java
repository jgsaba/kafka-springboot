package com.udemy.kafkaproducer.controller;

import com.udemy.kafkaproducer.LibraryEventProducer;
import com.udemy.kafkaproducer.persistence.LibraryEvent;
import com.udemy.kafkaproducer.persistence.LibraryEventType;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;


@RestController
@RequestMapping("/library")
@RequiredArgsConstructor
public class LibraryEventsController {

    private final LibraryEventProducer libraryEventProducer;

    @PostMapping("/v1/event")
    public void postLibraryEvent(@RequestBody LibraryEvent libraryEvent) throws ExecutionException, InterruptedException, TimeoutException {
        libraryEvent.setLibraryEventType(LibraryEventType.NEW);
        libraryEvent.setId(null);

        libraryEventProducer.sendLibraryEventSync(libraryEvent);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/v1/event")
    public void putLibraryEvent(@RequestBody LibraryEvent libraryEvent){
        libraryEvent.setLibraryEventType(LibraryEventType.UPDATE);
        libraryEventProducer.sendLibraryEvent(libraryEvent);
    }
}

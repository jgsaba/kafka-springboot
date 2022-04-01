package com.example.kafkaconsumer.service;

import com.example.kafkaconsumer.kafka.LibraryEventExtractor;
import com.example.kafkaconsumer.persistence.entities.LibraryEvent;
import com.example.kafkaconsumer.persistence.jpa.LibraryEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LibraryEventsService {

    private final LibraryEventRepository libraryEventRepository;

    public void processLibraryEvent(LibraryEvent libraryEvent){

        switch (libraryEvent.getLibraryEventType()){
            case NEW:
                saveEvent(libraryEvent);

            case UPDATE:
                updateEvent(libraryEvent);

            default:
                invalidEventType(libraryEvent);
        }

        log.info("Successfully processed library event {}", libraryEvent);
    }

    private void invalidEventType(LibraryEvent libraryEvent){
        log.error("Invalid event type. {}", libraryEvent);
        throw new RuntimeException();
    }

    private void updateEvent(LibraryEvent libraryEvent){
        if (null == libraryEvent.getId()){
            log.error("Library event id is null. {}", libraryEvent);
            throw new RuntimeException();
        }

        saveEvent(libraryEvent);
    }

    private void saveEvent(LibraryEvent libraryEvent){
        libraryEventRepository.save(libraryEvent);
    }
}

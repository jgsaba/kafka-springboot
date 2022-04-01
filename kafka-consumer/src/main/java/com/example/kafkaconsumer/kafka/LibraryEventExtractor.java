package com.example.kafkaconsumer.kafka;

import com.example.kafkaconsumer.persistence.entities.LibraryEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LibraryEventExtractor {

    private final ObjectMapper objectMapper;

    public LibraryEvent extract(String value){

        try {
            return extractValue(value);
        } catch (JsonProcessingException exception){
            log.error("Error trying to extract a LibraryEvent from record value={}", value);
            throw new RuntimeException(exception);
        }
    }

    private LibraryEvent extractValue(String value) throws JsonProcessingException {
        LibraryEvent libraryEvent = objectMapper.readValue(value, LibraryEvent.class);
        log.info("Extracted a LibraryEvent from record={}", libraryEvent);

        return libraryEvent;
    }
}

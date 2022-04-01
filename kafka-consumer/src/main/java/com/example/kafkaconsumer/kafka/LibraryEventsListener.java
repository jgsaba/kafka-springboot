package com.example.kafkaconsumer.kafka;

import com.example.kafkaconsumer.kafka.custom.CustomTopicListener;
import com.example.kafkaconsumer.service.LibraryEventsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "app.kafka", name = "manual-commit", havingValue = "false")
@RequiredArgsConstructor
public class LibraryEventsListener {

    private final LibraryEventsService libraryEventsService;

    private final LibraryEventExtractor libraryEventExtractor;

    static { log.info("Using manual commitment kafka listener"); }

    @CustomTopicListener
    public void onMessage(ConsumerRecord<String, String> consumerRecord){
        log.info("[LISTENER] New record received: {}", consumerRecord);
        throw new RuntimeException();
        //libraryEventsService.processLibraryEvent(libraryEventExtractor.extract(consumerRecord.value()));
    }

}

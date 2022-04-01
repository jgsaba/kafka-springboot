package com.example.kafkaconsumer.kafka.manual;

import com.example.kafkaconsumer.kafka.LibraryEventExtractor;
import com.example.kafkaconsumer.service.LibraryEventsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(prefix = "app.kafka", name = "manual-commit", havingValue = "true")
@RequiredArgsConstructor
@Component
@Slf4j
public class LibraryEventsListenerManualCommit implements AcknowledgingMessageListener<String, String> {

    private final LibraryEventsService libraryEventsService;

    private final LibraryEventExtractor libraryEventExtractor;

    static { log.info("Using kafka listener with manual commit"); }

    @Override
    public void onMessage(ConsumerRecord<String, String> consumerRecord, Acknowledgment acknowledgment) {
        log.info("New record received: {}", consumerRecord);
        libraryEventsService.processLibraryEvent(libraryEventExtractor.extract(consumerRecord.value()));
        acknowledgment.acknowledge();
    }
}

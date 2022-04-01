package com.udemy.kafkaproducer;

import com.udemy.kafkaproducer.persistence.LibraryEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
@RequiredArgsConstructor
public class LibraryEventProducer {

    private final KafkaTemplate<String, LibraryEvent> kafkaTemplate;

    public void sendLibraryEvent(LibraryEvent libraryEvent){

        kafkaTemplate.sendDefault(libraryEvent).addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {
                handleFailure(ex);
            }

            @Override
            public void onSuccess(SendResult<String, LibraryEvent> result) {
                handleSuccess();
            }
        });
    }

    public void sendLibraryEventSync(LibraryEvent libraryEvent) throws ExecutionException, InterruptedException, TimeoutException {
        SendResult<String, LibraryEvent> sendResult = kafkaTemplate.sendDefault(libraryEvent).get(1, TimeUnit.SECONDS);
        log.info("message sent sync");
        log.info("record: {}. metadata: {}", sendResult.getProducerRecord(), sendResult.getRecordMetadata());
    }

    public void sendEventByProducerRecord(LibraryEvent libraryEvent){
        kafkaTemplate.send(buildProducerRecord(libraryEvent));
    }

    private ProducerRecord<String, LibraryEvent> buildProducerRecord(LibraryEvent libraryEvent){
        return new ProducerRecord<>("library-events", null, UUID.randomUUID().toString(), libraryEvent, null);
    }

    private void handleSuccess(){
        log.info("message successfully sent");
    }

    private void handleFailure(Throwable ex){
        log.error("error sending message: {}", ex.getMessage());
    }
}

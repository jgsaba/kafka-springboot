package com.example.kafkaconsumer.kafka.custom;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomErrorHandler implements KafkaListenerErrorHandler {

    // executes before interceptor and default error handler

    @Override
    public Object handleError(Message<?> message, ListenerExecutionFailedException exception) {
        log.error("[CUSTOM ERROR HANDLER] Handling error in custom handler. Message: {} || Exception: {}", message, exception);

        ConsumerRecord<String, String> consumerRecord = (ConsumerRecord<String, String>) message.getPayload();
        // throw exception, so it will throw to the kafka container to be handled by default error handler
        throw exception;
    }
}

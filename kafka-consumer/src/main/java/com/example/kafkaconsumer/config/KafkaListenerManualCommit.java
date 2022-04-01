package com.example.kafkaconsumer.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.*;
import org.springframework.util.backoff.FixedBackOff;

import java.util.Map;

@Slf4j
@Configuration
public class KafkaListenerManualCommit {

    @Value("${spring.kafka.consumer.bootstrap-servers}")
    private String bootstrapServerAddress;

    @Autowired
    private KafkaProperties kafkaProperties;

    @Bean
    //@ConditionalOnProperty(prefix = "app.kafka", name = "manual-commit", havingValue = "false")
    ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(){

        log.info("initializing");

        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());

        factory.setRecordInterceptor(successFailureInterceptor());

        // adjust the default behaviour to try to reprocess the record 2 times every 3 seconds
        factory.setCommonErrorHandler(new DefaultErrorHandler(recoverer(), new FixedBackOff(0L, 0L)));

        return factory;
    }

    private DeadLetterPublishingRecoverer recoverer(){
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(new KafkaTemplate<>(producerFactory()));
        recoverer.excludeHeader(DeadLetterPublishingRecoverer.HeaderNames.HeadersToAdd.EX_STACKTRACE);
        recoverer.excludeHeader(DeadLetterPublishingRecoverer.HeaderNames.HeadersToAdd.EX_CAUSE);
        return recoverer;
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.kafka", name = "manual-commit", havingValue = "true")
    ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactoryManual(){
        ConcurrentKafkaListenerContainerFactory<String, String> factory = kafkaListenerContainerFactory();

        // manual commit, receive an Acknowledge as parameter in the listener
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        return factory;
    }

    private RecordInterceptor<String, String> successFailureInterceptor(){
        return new RecordInterceptor<String, String>() {
            @Override
            public ConsumerRecord<String, String> intercept(ConsumerRecord<String, String> record) {
                return record;
            }

            @Override
            public void success(ConsumerRecord<String, String> record, Consumer<String, String> consumer) {
                // executes after record successfully processed
                RecordInterceptor.super.success(record, consumer);
            }

            @Override
            public void failure(ConsumerRecord<String, String> record, Exception exception, Consumer<String, String> consumer) {
                // executes if record processing fails, before default error handler attempt, executes for each error handler retry
                log.error("[INTERCEPTOR] Error processing record {}. Exception {}, Consumer: {}", record, exception, consumer);
                RecordInterceptor.super.failure(record, exception, consumer);
            }
        };
    }

    // return record, it will be processed. return null, it will skip the message
    // useful for non-business processing as metrics, tracing etc
    private RecordInterceptor<String, String> recordInterceptor(){
        return record -> {
            return record;
        };
    }

    private ConsumerFactory<String, String> consumerFactory(){
        return new DefaultKafkaConsumerFactory<>(Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers(),
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class));
    }

    private ProducerFactory<String, String> producerFactory(){
        return new DefaultKafkaProducerFactory<>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers(),
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class));
    }
}

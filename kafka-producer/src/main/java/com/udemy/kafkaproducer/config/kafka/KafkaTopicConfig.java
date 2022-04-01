package com.udemy.kafkaproducer.config.kafka;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
@Profile("dev")
public class KafkaTopicConfig {

    @Bean
    public KafkaAdmin.NewTopics topics(){
        return new KafkaAdmin.NewTopics(
                TopicBuilder.name("library-events").partitions(2).replicas(3).build(),
                TopicBuilder.name("library-events.DLT").partitions(2).replicas(3).build());
    }
}

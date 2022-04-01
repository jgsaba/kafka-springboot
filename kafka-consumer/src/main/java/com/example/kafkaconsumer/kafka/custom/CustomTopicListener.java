package com.example.kafkaconsumer.kafka.custom;


import org.springframework.core.annotation.AliasFor;
import org.springframework.kafka.annotation.KafkaListener;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@KafkaListener
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomTopicListener {

    @AliasFor(annotation = KafkaListener.class, attribute = "groupId")
    String groupId() default "library-events-cg1";

    @AliasFor(annotation = KafkaListener.class, attribute = "topics")
    String[] topics() default "library-events";

    @AliasFor(annotation = KafkaListener.class, attribute = "containerFactory")
    String containerFactory() default "";

    @AliasFor(annotation = KafkaListener.class, attribute = "errorHandler")
    String errorHandler() default "customErrorHandler";
}

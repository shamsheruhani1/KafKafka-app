package com.learnkafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class AutoCreateConfig {


@Bean
    public NewTopic libraryEvent(){
        return TopicBuilder
                .name("library-event")
                .partitions(3)
                .replicas(3)
                .build();
    }
}

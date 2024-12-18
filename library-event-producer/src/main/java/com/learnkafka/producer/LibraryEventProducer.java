package com.learnkafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.domain.LibraryEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@Slf4j
public class LibraryEventProducer {

    private static final String TOPIC_NAME = "library-event";
    private final KafkaTemplate<Integer, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public LibraryEventProducer(KafkaTemplate<Integer, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Sends a library event asynchronously using KafkaTemplate.
     * Handles success and failure scenarios with callbacks.
     */
    public CompletableFuture<SendResult<Integer, String>> sendLibraryEventAsync(LibraryEvent libraryEvent) throws JsonProcessingException {
        Integer key = libraryEvent.libraryEventId();
        String value = objectMapper.writeValueAsString(libraryEvent);

        CompletableFuture<SendResult<Integer, String>> future = kafkaTemplate.send(TOPIC_NAME, key, value);
        return future.whenComplete((sendResult, throwable) -> {
            if (throwable != null) {
                handleFailure(key, value, throwable);
            } else {
                handleSuccess(key, value, sendResult);
            }
        });
    }

    /**
     * Sends a library event synchronously using KafkaTemplate.
     * Waits until the message is sent or times out after 3 seconds.
     */
    public SendResult<Integer, String> sendLibraryEventSync(LibraryEvent libraryEvent)
            throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        Integer key = libraryEvent.libraryEventId();
        String value = objectMapper.writeValueAsString(libraryEvent);

        SendResult<Integer, String> result = kafkaTemplate.send(TOPIC_NAME, key, value)
                .get(3, TimeUnit.SECONDS);
        handleSuccess(key, value, result);
        return result;
    }

    /**
     * Sends a library event using a custom ProducerRecord.
     * Handles success and failure scenarios asynchronously.
     */
    public CompletableFuture<SendResult<Integer, String>> sendLibraryEventWithProducerRecord(LibraryEvent libraryEvent) throws JsonProcessingException {
        Integer key = libraryEvent.libraryEventId();
        String value = objectMapper.writeValueAsString(libraryEvent);

        ProducerRecord<Integer, String> producerRecord = createProducerRecord(key, value);
        CompletableFuture<SendResult<Integer, String>> future = kafkaTemplate.send(producerRecord);

        return future.whenComplete((sendResult, throwable) -> {
            if (throwable != null) {
                handleFailure(key, value, throwable);
            } else {
                handleSuccess(key, value, sendResult);
            }
        });
    }

    /**
     * Creates a ProducerRecord for sending messages to Kafka.
     */
    private ProducerRecord<Integer, String> createProducerRecord(Integer key, String value) {
        List<Header> recordHeaders=List.of(
                new RecordHeader("Event-source","Scanner".getBytes()),
        new RecordHeader("Approach","Third Approach".getBytes())
        );

        return new ProducerRecord<>(TOPIC_NAME, null,key, value,recordHeaders);
    }

    /**
     * Logs success details when a message is sent successfully.
     */
    private void handleSuccess(Integer key, String value, SendResult<Integer, String> sendResult) {
        log.info("Message sent successfully for key: {}, value: {}, partition: {}",
                key, value, sendResult.getRecordMetadata().partition());
    }

    /**
     * Logs error details when a message fails to send.
     */
    private void handleFailure(Integer key, String value, Throwable throwable) {
        log.error("Failed to send message for key: {}, value: {}, exception: {}",
                key, value, throwable.getMessage(), throwable);
    }
}
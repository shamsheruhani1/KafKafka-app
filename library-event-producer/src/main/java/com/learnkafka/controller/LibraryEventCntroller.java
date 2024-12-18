package com.learnkafka.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.learnkafka.domain.LibraryEvent;
import com.learnkafka.domain.LibraryEventType;
import com.learnkafka.producer.LibraryEventProducer;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@Slf4j
public class LibraryEventCntroller {
LibraryEventProducer libraryEventProducer;

    public LibraryEventCntroller(LibraryEventProducer libraryEventProducer) {
        this.libraryEventProducer = libraryEventProducer;
    }

    @PostMapping("/v1/libraryevent")
    public ResponseEntity<LibraryEvent> postLibraryEvent(@RequestBody @Valid LibraryEvent libraryEvent ) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {

        log.info("logger events : {}",libraryEvent );

        libraryEventProducer.sendLibraryEventWithProducerRecord(libraryEvent);
        log.info("after sending message to topic" );
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

    @PutMapping("/v1/libraryevent")
    public ResponseEntity<?> updateLibraryEvent(@RequestBody @Valid LibraryEvent libraryEvent ) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {

        log.info("logger events : {}",libraryEvent );
        ResponseEntity<String> BAD_REQUEST = validateLibraryEvent(libraryEvent);
        if (BAD_REQUEST != null) return BAD_REQUEST;
        libraryEventProducer.sendLibraryEventWithProducerRecord(libraryEvent);
        log.info("after sending message to topic" );
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }




    private static ResponseEntity<String> validateLibraryEvent(LibraryEvent libraryEvent) {
        if(libraryEvent.libraryEventId()==null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("plz provide library Event id");
        }
        if(!libraryEvent.libraryEventType().equals(LibraryEventType.UPDATE)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("only ypdate Event allow");

        }
        return null;
    }
}

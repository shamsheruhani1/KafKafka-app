package com.learnkafka.controller;

import com.learnkafka.domain.LibraryEvent;
import com.learnkafka.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LibraryEventCntrollerIntegartionTest {

    @Autowired
    TestRestTemplate restTemplate;
    @Test
    void postLibraryEvent() {
        HttpHeaders header= new HttpHeaders();
        header.set("content-type", MediaType.APPLICATION_JSON.toString());
        HttpEntity<LibraryEvent> httpEntity= new HttpEntity<>(TestUtil.libraryEventRecord());
      ResponseEntity<LibraryEvent> responseEntity= restTemplate.exchange("/v1/libraryevent", HttpMethod.POST,httpEntity,LibraryEvent.class);

      assertEquals(HttpStatus.CREATED,responseEntity.getStatusCode());
    }
}
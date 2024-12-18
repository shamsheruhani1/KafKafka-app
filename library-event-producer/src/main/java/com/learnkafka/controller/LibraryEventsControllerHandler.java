package com.learnkafka.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class LibraryEventsControllerHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?>  handleRequest(MethodArgumentNotValidException ex){

     String errorMessage=   ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField()+" - "+fieldError.getDefaultMessage())
                .sorted()
                .collect(Collectors.joining(","));
     log.info("error message :{}",errorMessage);
return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }
}

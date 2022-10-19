package com.example.intermediate.controller;

import com.example.intermediate.domain.Error;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.ArrayList;
import java.util.List;
@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity handleValidationExceptions(MethodArgumentNotValidException exception) {

        List<Error> errors = new ArrayList<>();

        for(FieldError field : exception.getBindingResult().getFieldErrors()) {
            errors.add(new Error(field.getField(), field.getDefaultMessage()));
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errors);
  }
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    protected ResponseEntity handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException e) {
        log.info("handleMaxUploadSizeExceededException", e);

        return new ResponseEntity<>("ERROR", HttpStatus.BAD_REQUEST);
    }
}
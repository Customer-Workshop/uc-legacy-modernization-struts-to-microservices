package com.northstar.workbench.exception;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
  @ExceptionHandler(NotFoundException.class)
  ResponseEntity<Map<String, List<String>>> missing(NotFoundException ex) {
    return ResponseEntity.status(404).body(Map.of("validationErrors", List.of(ex.getMessage())));
  }
}

package com.northstar.intake.exception;

import com.northstar.intake.dto.ErrorResponse;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
  @ExceptionHandler(NotFoundException.class)
  ResponseEntity<ErrorResponse> missing(NotFoundException ex) {
    return ResponseEntity.status(404).body(new ErrorResponse(List.of(ex.getMessage())));
  }
}

package com.northstar.policy.controller;

import com.northstar.policy.dto.ErrorResponse;
import com.northstar.policy.dto.LoginRequest;
import com.northstar.policy.dto.LoginResponse;
import com.northstar.policy.service.AuthService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService service;

  public AuthController(AuthService service) {
    this.service = service;
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
    if (!service.authenticate(request.username(), request.password())) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new ErrorResponse(List.of("login.failed")));
    }
    return ResponseEntity.ok(new LoginResponse(request.username()));
  }
}

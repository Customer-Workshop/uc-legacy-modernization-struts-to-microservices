package com.northstar.intake.controller;

import com.northstar.intake.service.ClaimApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal")
public class AdminController {
  private final ClaimApplicationService service;

  public AdminController(ClaimApplicationService service) {
    this.service = service;
  }

  @PostMapping("/reset")
  public ResponseEntity<Void> reset() {
    service.reset();
    return ResponseEntity.noContent().build();
  }
}

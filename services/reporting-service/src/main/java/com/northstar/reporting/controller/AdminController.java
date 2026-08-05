package com.northstar.reporting.controller;

import com.northstar.reporting.service.ReportApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
public class AdminController {
  private final ReportApplicationService service;

  public AdminController(ReportApplicationService service) {
    this.service = service;
  }

  @PostMapping("/reset")
  public ResponseEntity<Void> reset() {
    service.reset();
    return ResponseEntity.noContent().build();
  }
}

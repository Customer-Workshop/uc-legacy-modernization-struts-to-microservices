package com.northstar.settlement.controller;

import com.northstar.settlement.service.SettlementApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal")
public class AdminController {
  private final SettlementApplicationService service;

  public AdminController(SettlementApplicationService service) {
    this.service = service;
  }

  @PostMapping("/reset")
  public ResponseEntity<Void> reset() {
    service.reset();
    return ResponseEntity.noContent().build();
  }
}

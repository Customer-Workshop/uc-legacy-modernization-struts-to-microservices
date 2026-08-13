package com.northstar.settlement.controller;

import com.northstar.settlement.dto.*;
import com.northstar.settlement.service.SettlementApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settlements")
public class SettlementController {
  private final SettlementApplicationService service;

  public SettlementController(SettlementApplicationService service) {
    this.service = service;
  }

  @PostMapping("/calculate")
  public SettlementResponse calculate(@RequestBody SettlementRequest request) {
    return SettlementResponse.calculated(service.calculate(request));
  }

  @PostMapping
  public ResponseEntity<SettlementResponse> save(@RequestBody SettlementRequest request) {
    return ResponseEntity.ok(SettlementResponse.saved(service.save(request)));
  }

  @GetMapping("/claims/{claimId}")
  public SettlementResponse latest(@PathVariable int claimId) {
    return SettlementResponse.latest(service.latest(claimId));
  }
}

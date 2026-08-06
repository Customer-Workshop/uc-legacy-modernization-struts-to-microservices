package com.northstar.settlement.controller;

import com.northstar.settlement.dto.CalculateRequest;
import com.northstar.settlement.dto.SettlementResponse;
import com.northstar.settlement.service.SettlementApplicationService;
import java.util.LinkedHashMap;
import java.util.Map;
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
  public SettlementResponse calculate(@RequestBody CalculateRequest request) {
    return SettlementResponse.from(service.calculate(request), service.claimId(request.claimId()));
  }

  @PostMapping
  public ResponseEntity<SettlementResponse> save(@RequestBody CalculateRequest request) {
    return ResponseEntity.status(201).body(SettlementResponse.from(service.save(request)));
  }

  /** Latest settlement amount per claim, used by the parity DB probe. */
  @GetMapping("/claim")
  public Map<String, Map<String, Double>> latestByClaim() {
    Map<String, Map<String, Double>> byClaim = new LinkedHashMap<>();
    for (var settlement : service.allSettlements()) {
      byClaim.put(
          settlement.getClaimId().toString(), Map.of("amount", settlement.getSettlementAmount()));
    }
    return byClaim;
  }

  @GetMapping("/claim/{claimId}")
  public SettlementResponse latest(@PathVariable int claimId) {
    return SettlementResponse.from(service.latestForClaim(claimId));
  }
}

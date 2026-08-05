package com.northstar.settlement.controller;

import com.northstar.settlement.dto.SettlementRequest;
import com.northstar.settlement.dto.SettlementResponse;
import com.northstar.settlement.dto.SettlementSaveResponse;
import com.northstar.settlement.service.SettlementApplicationService;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/settlements")
public class SettlementController {
  private final SettlementApplicationService service;

  public SettlementController(SettlementApplicationService service) {
    this.service = service;
  }

  @PostMapping("/calculate")
  public SettlementResponse calculate(@RequestBody SettlementRequest request) {
    var result = service.calculate(request);
    return SettlementResponse.from(result.settlement(), result.policyLimit());
  }

  @PostMapping
  public SettlementSaveResponse save(@RequestBody SettlementRequest request) {
    return SettlementSaveResponse.from(service.save(request));
  }

  @GetMapping("/latest-by-claim")
  public Map<String, Map<String, Double>> latestByClaim() {
    Map<String, Map<String, Double>> body = new java.util.LinkedHashMap<>();
    service
        .latestAmountsByClaim()
        .forEach((claimId, amount) -> body.put(claimId, Map.of("amount", amount)));
    return body;
  }
}

package com.northstar.settlement.controller;

import com.northstar.settlement.dto.CalculationResponse;
import com.northstar.settlement.dto.SettlementRequest;
import com.northstar.settlement.dto.SettlementResponse;
import com.northstar.settlement.service.SettlementApplicationService;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/settlements")
public class SettlementController {
  private final SettlementApplicationService service;

  public SettlementController(SettlementApplicationService service) {
    this.service = service;
  }

  @PostMapping("/calculate")
  public CalculationResponse calculate(@RequestBody SettlementRequest request) {
    return service.calculate(request);
  }

  @PostMapping
  public ResponseEntity<SettlementResponse> save(@RequestBody SettlementRequest request) {
    return ResponseEntity.status(201).body(SettlementResponse.from(service.save(request)));
  }

  @GetMapping("/detail")
  public SettlementResponse detail(@RequestParam(required = false) String claimId) {
    return SettlementResponse.from(service.settlementDetail(claimId));
  }

  @GetMapping("/by-claim")
  public Map<String, SettlementResponse> byClaim() {
    Map<String, SettlementResponse> view = new LinkedHashMap<>();
    service.latestByClaim().forEach((claim, s) -> view.put(claim, SettlementResponse.from(s)));
    return view;
  }
}

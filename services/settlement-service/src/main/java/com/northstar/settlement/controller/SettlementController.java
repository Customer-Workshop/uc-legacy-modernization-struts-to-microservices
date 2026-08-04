package com.northstar.settlement.controller;

import com.northstar.settlement.dto.SettlementCalculateRequest;
import com.northstar.settlement.dto.SettlementCalculationResponse;
import com.northstar.settlement.dto.SettlementClaimSummary;
import com.northstar.settlement.dto.SettlementDetailResponse;
import com.northstar.settlement.dto.SettlementSaveRequest;
import com.northstar.settlement.dto.SettlementSaveResponse;
import com.northstar.settlement.service.SettlementApplicationService;
import java.util.Map;
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
  public SettlementCalculationResponse calculate(@RequestBody SettlementCalculateRequest request) {
    return SettlementCalculationResponse.from(service.calculate(request));
  }

  @PostMapping
  public SettlementSaveResponse save(@RequestBody SettlementSaveRequest request) {
    return SettlementSaveResponse.from(service.save(request));
  }

  @GetMapping("/claim")
  public Map<String, SettlementClaimSummary> claim() {
    return service.settlementClaims();
  }

  @GetMapping("/detail")
  public SettlementDetailResponse detail(@RequestParam(required = false) String claimId) {
    int id = service.integer(claimId, 119);
    return SettlementDetailResponse.from(service.detail(id));
  }
}

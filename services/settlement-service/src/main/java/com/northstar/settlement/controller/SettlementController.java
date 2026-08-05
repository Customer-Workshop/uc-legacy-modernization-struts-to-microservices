package com.northstar.settlement.controller;

import com.northstar.settlement.dto.SettlementDetailResponse;
import com.northstar.settlement.dto.SettlementRequest;
import com.northstar.settlement.dto.SettlementResponse;
import com.northstar.settlement.dto.SettlementSaveResponse;
import com.northstar.settlement.service.SettlementApplicationService;
import java.util.LinkedHashMap;
import java.util.Map;
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
    return SettlementResponse.from(service.calculate(request));
  }

  @PostMapping
  public SettlementSaveResponse save(@RequestBody SettlementRequest request) {
    return SettlementSaveResponse.from(service.save(request));
  }

  /** Latest settlement per claim, keyed by claim id, used by the parity DB probes. */
  @GetMapping("/claim")
  public Map<String, SettlementDetailResponse> latestByClaim() {
    Map<String, SettlementDetailResponse> byClaim = new LinkedHashMap<>();
    service
        .all()
        .forEach(s -> byClaim.put(s.getClaimId().toString(), SettlementDetailResponse.from(s)));
    return byClaim;
  }

  @GetMapping("/claim/{claimId}")
  public SettlementDetailResponse latest(@PathVariable int claimId) {
    return SettlementDetailResponse.from(service.latestByClaim(claimId));
  }
}

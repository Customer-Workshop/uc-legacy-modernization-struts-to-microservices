package com.northstar.settlement.controller;

import com.northstar.settlement.dto.SettlementCalcRequest;
import com.northstar.settlement.dto.SettlementCalcResponse;
import com.northstar.settlement.dto.SettlementResponse;
import com.northstar.settlement.dto.SettlementSaveResponse;
import com.northstar.settlement.model.Settlement;
import com.northstar.settlement.service.LegacyCoercions;
import com.northstar.settlement.service.LegacyMoney;
import com.northstar.settlement.service.SettlementApplicationService;
import com.northstar.settlement.service.SettlementCalculator;
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
  public SettlementCalcResponse calculate(@RequestBody SettlementCalcRequest request) {
    SettlementCalculator.Result result = service.calculate(request);
    return new SettlementCalcResponse(
        String.valueOf(LegacyCoercions.integer(request.claimId(), 119)),
        LegacyMoney.format(result.coveredAmount()),
        LegacyMoney.format(result.deductibleApplied()),
        LegacyMoney.format(result.depreciation()),
        String.valueOf(result.cappedAtLimit()),
        LegacyMoney.format(result.settlementAmount()));
  }

  @PostMapping
  public SettlementSaveResponse save(@RequestBody SettlementCalcRequest request) {
    Settlement saved = service.save(request);
    return new SettlementSaveResponse(
        saved.getSettlementId().toString(),
        saved.getClaimId().toString(),
        LegacyMoney.format(saved.getSettlementAmount()),
        saved.getCalculatedBy());
  }

  @GetMapping("/claim")
  public Map<String, Map<String, String>> latestByClaim() {
    return service.latestAmountsByClaim();
  }

  @GetMapping("/{id}")
  public SettlementResponse get(@PathVariable int id) {
    return SettlementResponse.from(service.settlement(id));
  }
}

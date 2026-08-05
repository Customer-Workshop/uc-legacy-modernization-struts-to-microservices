package com.northstar.settlement.controller;

import com.northstar.settlement.dto.SettlementRequest;
import com.northstar.settlement.dto.SettlementResponse;
import com.northstar.settlement.dto.SettlementSaveResponse;
import com.northstar.settlement.model.Settlement;
import com.northstar.settlement.service.SettlementApplicationService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
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
    return SettlementResponse.from(service.calculate(request));
  }

  @PostMapping
  public ResponseEntity<SettlementSaveResponse> save(@RequestBody SettlementRequest request) {
    // legacy-faithful: the recorded screens always ran under the supervisor session user.
    Settlement value = service.save(request, "supervisor");
    return ResponseEntity.status(201)
        .body(
            new SettlementSaveResponse(
                value.getSettlementId(),
                value.getClaimId(),
                value.getSettlementAmount(),
                value.getCalculatedBy()));
  }

  @GetMapping("/by-claim")
  public Map<String, Map<String, Object>> byClaim() {
    return service.latestByClaim();
  }
}

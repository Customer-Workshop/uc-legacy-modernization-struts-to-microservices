package com.northstar.settlement.controller;

import com.northstar.settlement.dto.PaymentRequest;
import com.northstar.settlement.dto.PaymentResponse;
import com.northstar.settlement.service.SettlementApplicationService;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
  private final SettlementApplicationService service;

  public PaymentController(SettlementApplicationService service) {
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<PaymentResponse> issue(@RequestBody PaymentRequest request) {
    return ResponseEntity.status(201).body(PaymentResponse.from(service.issue(request)));
  }

  @GetMapping
  public List<PaymentResponse> history(@RequestParam(required = false) String claimId) {
    return service.history(claimId).stream().map(PaymentResponse::from).toList();
  }

  /** Payment counts per claim, used by the parity DB probe. */
  @GetMapping("/count")
  public Map<String, Map<String, Integer>> counts() {
    Map<String, Integer> byClaim = new LinkedHashMap<>();
    for (var payment : service.allPayments()) {
      byClaim.merge(payment.getClaimId().toString(), 1, Integer::sum);
    }
    return Map.of("claim", byClaim);
  }

  @GetMapping("/{id:\\d+}")
  public PaymentResponse get(@PathVariable int id) {
    return PaymentResponse.from(service.payment(id));
  }
}

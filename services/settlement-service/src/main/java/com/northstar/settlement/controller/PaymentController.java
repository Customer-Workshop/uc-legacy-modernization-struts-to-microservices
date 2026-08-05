package com.northstar.settlement.controller;

import com.northstar.settlement.dto.PaymentRequest;
import com.northstar.settlement.dto.PaymentResponse;
import com.northstar.settlement.service.SettlementApplicationService;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
  private final SettlementApplicationService service;

  public PaymentController(SettlementApplicationService service) {
    this.service = service;
  }

  @PostMapping
  public PaymentResponse issue(@RequestBody PaymentRequest request) {
    return PaymentResponse.from(service.issuePayment(request));
  }

  @GetMapping
  public List<PaymentResponse> history(@RequestParam(required = false) String claimId) {
    return service.history(claimId).stream().map(PaymentResponse::from).toList();
  }

  /** Payment counts per claim, used by the parity DB probes. */
  @GetMapping("/count")
  public Map<String, Map<String, Integer>> count() {
    Map<String, Integer> byClaim = new LinkedHashMap<>();
    service.allPayments().forEach(p -> byClaim.merge(p.getClaimId().toString(), 1, Integer::sum));
    return Map.of("claim", byClaim);
  }

  @GetMapping("/{id}")
  public PaymentResponse get(@PathVariable int id) {
    return PaymentResponse.from(service.getPayment(id));
  }
}

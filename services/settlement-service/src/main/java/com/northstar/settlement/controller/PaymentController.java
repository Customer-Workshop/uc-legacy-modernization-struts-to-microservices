package com.northstar.settlement.controller;

import com.northstar.settlement.dto.PaymentIssueRequest;
import com.northstar.settlement.dto.PaymentIssueResponse;
import com.northstar.settlement.dto.PaymentResponse;
import com.northstar.settlement.model.Payment;
import com.northstar.settlement.service.SettlementApplicationService;
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
  public PaymentIssueResponse issue(@RequestBody PaymentIssueRequest request) {
    Payment payment = service.issue(request);
    PaymentResponse view = PaymentResponse.from(payment);
    return new PaymentIssueResponse(
        view.paymentId().toString(),
        view.claimId().toString(),
        view.amount(),
        view.checkNumber(),
        view.status());
  }

  @GetMapping
  public List<PaymentResponse> history(@RequestParam(required = false) String claimId) {
    return service.history(claimId).stream().map(PaymentResponse::from).toList();
  }

  @GetMapping("/count")
  public Map<String, Map<String, Long>> counts() {
    return service.countsByClaim();
  }

  @GetMapping("/{id}")
  public PaymentResponse get(@PathVariable int id) {
    return PaymentResponse.from(service.payment(id));
  }
}

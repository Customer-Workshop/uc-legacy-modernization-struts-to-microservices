package com.northstar.settlement.controller;

import com.northstar.settlement.dto.PaymentHistoryResponse;
import com.northstar.settlement.dto.PaymentIssueResponse;
import com.northstar.settlement.dto.PaymentRequest;
import com.northstar.settlement.dto.PaymentResponse;
import com.northstar.settlement.service.SettlementApplicationService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
  private final SettlementApplicationService service;

  public PaymentController(SettlementApplicationService service) {
    this.service = service;
  }

  @PostMapping
  public PaymentIssueResponse issue(@RequestBody PaymentRequest request) {
    return PaymentIssueResponse.from(service.issue(request));
  }

  @GetMapping
  public PaymentHistoryResponse history(
      @RequestParam(name = "claimId", required = false) String claimId) {
    var history = service.history(claimId);
    List<PaymentResponse> payments =
        history.payments().stream().map(PaymentResponse::from).toList();
    return new PaymentHistoryResponse(history.claimId(), payments.size(), payments);
  }

  @GetMapping("/count")
  public Map<String, Map<String, Long>> counts() {
    return Map.of("claim", service.paymentCountsByClaim());
  }

  @GetMapping("/{id:\\d+}")
  public PaymentResponse get(@PathVariable int id) {
    return PaymentResponse.from(service.getPayment(id));
  }
}

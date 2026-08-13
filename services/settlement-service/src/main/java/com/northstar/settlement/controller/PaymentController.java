package com.northstar.settlement.controller;

import com.northstar.settlement.dto.*;
import com.northstar.settlement.service.SettlementApplicationService;
import java.util.List;
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
    return PaymentResponse.from(service.issue(request));
  }

  @GetMapping
  public List<PaymentResponse> list(@RequestParam(required = false) String claimId) {
    return service.list(claimId).stream().map(PaymentResponse::from).toList();
  }

  @GetMapping("/{paymentId}")
  public PaymentResponse get(@PathVariable int paymentId) {
    return PaymentResponse.from(service.getPayment(paymentId));
  }
}

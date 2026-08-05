package com.northstar.settlement.controller;

import com.northstar.settlement.dto.PaymentRequest;
import com.northstar.settlement.dto.PaymentResponse;
import com.northstar.settlement.service.PaymentApplicationService;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
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
  private final PaymentApplicationService service;

  public PaymentController(PaymentApplicationService service) {
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

  @GetMapping("/count")
  public Map<String, Map<String, Long>> counts() {
    return service.countsByClaim();
  }

  @GetMapping("/{id}")
  public PaymentResponse get(@PathVariable int id) {
    return PaymentResponse.from(service.get(id));
  }
}

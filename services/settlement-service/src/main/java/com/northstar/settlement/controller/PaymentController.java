package com.northstar.settlement.controller;

import com.northstar.settlement.dto.PaymentIssueRequest;
import com.northstar.settlement.dto.PaymentListResponse;
import com.northstar.settlement.dto.PaymentResponse;
import com.northstar.settlement.dto.RemittanceResponse;
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
  public PaymentResponse issue(@RequestBody PaymentIssueRequest request) {
    return PaymentResponse.from(service.issue(request));
  }

  @GetMapping("/count")
  public Map<String, Map<String, Integer>> count() {
    return service.paymentCounts();
  }

  @GetMapping("/remittance")
  public RemittanceResponse remittance(@RequestParam(required = false) String claimId) {
    int id = service.integer(claimId, 119);
    List<com.northstar.settlement.model.Payment> values = service.remittance(claimId);
    return RemittanceResponse.from(id, values, service.paymentTotal(claimId));
  }

  @GetMapping
  public PaymentListResponse history(@RequestParam(required = false) String claimId) {
    int id = service.integer(claimId, 119);
    return PaymentListResponse.from(id, service.paymentHistory(claimId));
  }

  @GetMapping("/{id}")
  public PaymentResponse detail(@PathVariable int id) {
    return PaymentResponse.from(service.paymentDetail(id));
  }
}

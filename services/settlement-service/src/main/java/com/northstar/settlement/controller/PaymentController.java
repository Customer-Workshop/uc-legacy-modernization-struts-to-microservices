package com.northstar.settlement.controller;

import com.northstar.settlement.dto.PaymentRequest;
import com.northstar.settlement.dto.PaymentResponse;
import com.northstar.settlement.dto.RemittanceResponse;
import com.northstar.settlement.service.SettlementApplicationService;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
  private final SettlementApplicationService service;

  public PaymentController(SettlementApplicationService service) {
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<PaymentResponse> issue(@RequestBody PaymentRequest request) {
    return ResponseEntity.status(201).body(PaymentResponse.from(service.issuePayment(request)));
  }

  @GetMapping
  public List<PaymentResponse> history(@RequestParam(required = false) String claimId) {
    return service.history(claimId).stream().map(PaymentResponse::from).toList();
  }

  @GetMapping("/count")
  public Map<String, Map<String, Long>> countByClaim() {
    return Map.of("claim", service.paymentCountsByClaim());
  }

  @GetMapping("/remittance")
  public RemittanceResponse remittance(@RequestParam(required = false) String claimId) {
    int id = service.integer(claimId, 119);
    List<PaymentResponse> rows =
        service.history(claimId).stream().map(PaymentResponse::from).toList();
    String total =
        BigDecimal.valueOf(service.totalIssued(id))
            .setScale(2, RoundingMode.HALF_UP)
            .toPlainString();
    return new RemittanceResponse(id, rows.size(), total, rows);
  }

  @GetMapping("/{id}")
  public PaymentResponse detail(@PathVariable String id) {
    return PaymentResponse.from(service.paymentDetail(id));
  }
}

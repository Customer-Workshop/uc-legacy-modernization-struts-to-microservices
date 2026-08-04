package com.northstar.settlement.dto;

import com.northstar.settlement.model.Payment;
import java.util.List;

public record RemittanceResponse(
    int claimId,
    int paymentCount,
    double paymentTotal,
    List<PaymentResponse> payments,
    List<String> validationErrors) {
  public static RemittanceResponse from(int claimId, List<Payment> payments, double total) {
    return new RemittanceResponse(
        claimId,
        payments.size(),
        total,
        payments.stream().map(PaymentResponse::from).toList(),
        List.of());
  }
}

package com.northstar.settlement.dto;

import com.northstar.settlement.model.Payment;
import java.util.List;

public record PaymentListResponse(
    int claimId, int paymentCount, List<PaymentResponse> payments, List<String> validationErrors) {
  public static PaymentListResponse from(int claimId, List<Payment> payments) {
    return new PaymentListResponse(
        claimId, payments.size(), payments.stream().map(PaymentResponse::from).toList(), List.of());
  }
}

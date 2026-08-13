package com.northstar.settlement.dto;

import com.northstar.settlement.model.Payment;
import java.util.List;
import java.util.Locale;

public record PaymentResponse(
    Integer paymentId,
    String checkNumber,
    String paymentAmount,
    String paymentStatus,
    List<String> validationErrors) {
  public static PaymentResponse from(Payment p) {
    return new PaymentResponse(
        p.getPaymentId(),
        p.getCheckNumber(),
        String.format(Locale.US, "%.2f", p.getAmount()),
        p.getStatus(),
        List.of());
  }
}

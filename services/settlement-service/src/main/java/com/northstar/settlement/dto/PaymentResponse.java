package com.northstar.settlement.dto;

import com.northstar.settlement.model.Payment;
import java.time.LocalDate;
import java.util.List;

public record PaymentResponse(
    int paymentId,
    int claimId,
    String checkNumber,
    double paymentAmount,
    String paymentStatus,
    String payeeName,
    String paymentMethod,
    LocalDate issuedDate,
    List<String> validationErrors) {
  public static PaymentResponse from(Payment payment) {
    return new PaymentResponse(
        payment.getPaymentId(),
        payment.getClaimId(),
        payment.getCheckNumber(),
        payment.getAmount(),
        payment.getStatus(),
        payment.getPayeeName(),
        payment.getPaymentMethod(),
        payment.getIssuedDate(),
        List.of());
  }
}

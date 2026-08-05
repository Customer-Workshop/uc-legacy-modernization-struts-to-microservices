package com.northstar.settlement.dto;

import com.northstar.settlement.model.Payment;

public record PaymentResponse(
    String paymentId,
    String claimId,
    String settlementId,
    String payeeName,
    String paymentAmount,
    String paymentMethod,
    String checkNumber,
    String issuedDate,
    String paymentStatus) {
  public static PaymentResponse from(Payment p) {
    return new PaymentResponse(
        p.getPaymentId().toString(),
        p.getClaimId().toString(),
        p.getSettlementId().toString(),
        p.getPayeeName(),
        MoneyText.of(p.getAmount().doubleValue()),
        p.getPaymentMethod(),
        p.getCheckNumber(),
        p.getIssuedDate().toString(),
        p.getStatus());
  }
}

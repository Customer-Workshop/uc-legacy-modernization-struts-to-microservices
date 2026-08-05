package com.northstar.settlement.dto;

import com.northstar.settlement.model.Payment;

public record PaymentResponse(
    Integer paymentId,
    Integer claimId,
    Integer settlementId,
    String payeeName,
    Double amount,
    String paymentMethod,
    String checkNumber,
    String issuedDate,
    String status) {
  public static PaymentResponse from(Payment value) {
    return new PaymentResponse(
        value.getPaymentId(),
        value.getClaimId(),
        value.getSettlementId(),
        value.getPayeeName(),
        value.getAmount(),
        value.getPaymentMethod(),
        value.getCheckNumber(),
        value.getIssuedDate().toString(),
        value.getStatus());
  }
}

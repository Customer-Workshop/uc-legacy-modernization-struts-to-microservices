package com.northstar.settlement.dto;

import com.northstar.settlement.model.Payment;

public record PaymentIssueResponse(
    Integer paymentId,
    Integer claimId,
    Integer settlementId,
    Double paymentAmount,
    String checkNumber,
    String paymentStatus) {
  public static PaymentIssueResponse from(Payment value) {
    return new PaymentIssueResponse(
        value.getPaymentId(),
        value.getClaimId(),
        value.getSettlementId(),
        value.getAmount(),
        value.getCheckNumber(),
        value.getStatus());
  }
}

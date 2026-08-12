package com.northstar.settlement.dto;

import com.northstar.settlement.model.Payment;
import com.northstar.settlement.service.LegacyMoney;

public record PaymentResponse(
    Integer paymentId,
    Integer claimId,
    Integer settlementId,
    String payeeName,
    String amount,
    String paymentMethod,
    String checkNumber,
    String issuedDate,
    String status) {
  public static PaymentResponse from(Payment p) {
    return new PaymentResponse(
        p.getPaymentId(),
        p.getClaimId(),
        p.getSettlementId(),
        p.getPayeeName(),
        LegacyMoney.format(p.getAmount()),
        p.getPaymentMethod(),
        p.getCheckNumber(),
        p.getIssuedDate().toString(),
        p.getStatus());
  }
}

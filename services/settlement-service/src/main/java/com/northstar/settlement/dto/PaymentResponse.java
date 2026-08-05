package com.northstar.settlement.dto;

import com.northstar.settlement.model.Payment;
import java.math.RoundingMode;

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
        p.getAmount().setScale(2, RoundingMode.HALF_UP).toPlainString(),
        p.getPaymentMethod(),
        p.getCheckNumber(),
        p.getIssuedDate().toString(),
        p.getStatus());
  }
}

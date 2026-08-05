package com.northstar.settlement.dto;

import com.northstar.settlement.model.Settlement;

public record SettlementResponse(
    Integer claimId,
    double coveredAmount,
    double deductibleApplied,
    double depreciation,
    String cappedAtLimit,
    double settlementAmount) {

  public static SettlementResponse from(Settlement value) {
    return new SettlementResponse(
        value.getClaimId(),
        value.getCoveredAmount(),
        value.getDeductibleApplied(),
        value.getDepreciation(),
        String.valueOf(value.isCappedAtLimit()),
        value.getSettlementAmount());
  }
}

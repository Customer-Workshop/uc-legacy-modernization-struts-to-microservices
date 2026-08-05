package com.northstar.settlement.dto;

import com.northstar.settlement.model.Settlement;

public record SettlementResponse(
    Integer claimId,
    Double policyLimit,
    Double coveredAmount,
    Double deductibleApplied,
    Double depreciation,
    String cappedAtLimit,
    Double settlementAmount) {
  public static SettlementResponse from(Settlement value, double policyLimit) {
    return new SettlementResponse(
        value.getClaimId(),
        policyLimit,
        value.getCoveredAmount(),
        value.getDeductibleApplied(),
        value.getDepreciation(),
        String.valueOf(value.getCappedAtLimit()),
        value.getSettlementAmount());
  }
}

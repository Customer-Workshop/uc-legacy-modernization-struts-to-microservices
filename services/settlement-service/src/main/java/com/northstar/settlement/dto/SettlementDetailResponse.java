package com.northstar.settlement.dto;

import com.northstar.settlement.model.Settlement;

public record SettlementDetailResponse(
    int settlementId,
    int claimId,
    double amount,
    String savedBy,
    double coveredAmount,
    double deductibleApplied,
    double depreciation,
    String cappedAtLimit,
    double settlementAmount) {
  public static SettlementDetailResponse from(Settlement value) {
    return new SettlementDetailResponse(
        value.getSettlementId(),
        value.getClaimId(),
        value.getSettlementAmount(),
        value.getCalculatedBy(),
        value.getCoveredAmount(),
        value.getDeductibleApplied(),
        value.getDepreciation(),
        Boolean.toString(value.isCappedAtLimit()),
        value.getSettlementAmount());
  }
}

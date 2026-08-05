package com.northstar.settlement.dto;

import com.northstar.settlement.model.Settlement;

public record SettlementSaveResponse(
    Integer settlementId,
    Integer claimId,
    String savedBy,
    String calculatedDate,
    Double coveredAmount,
    Double deductibleApplied,
    Double depreciation,
    String cappedAtLimit,
    Double settlementAmount) {
  public static SettlementSaveResponse from(Settlement value) {
    return new SettlementSaveResponse(
        value.getSettlementId(),
        value.getClaimId(),
        value.getCalculatedBy(),
        value.getCalculatedDate().toString(),
        value.getCoveredAmount(),
        value.getDeductibleApplied(),
        value.getDepreciation(),
        String.valueOf(value.getCappedAtLimit()),
        value.getSettlementAmount());
  }
}

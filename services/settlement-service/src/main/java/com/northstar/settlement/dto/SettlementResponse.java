package com.northstar.settlement.dto;

import com.northstar.settlement.model.Settlement;
import com.northstar.settlement.service.SettlementCalculator;

public record SettlementResponse(
    Integer settlementId,
    Integer claimId,
    double coveredAmount,
    double deductibleApplied,
    double depreciation,
    String cappedAtLimit,
    double settlementAmount,
    String calculatedBy,
    String calculatedDate) {
  public static SettlementResponse from(SettlementCalculator.Calculation value, int claimId) {
    return new SettlementResponse(
        null,
        claimId,
        value.coveredAmount(),
        value.deductibleApplied(),
        value.depreciation(),
        String.valueOf(value.cappedAtLimit()),
        value.settlementAmount(),
        null,
        null);
  }

  public static SettlementResponse from(Settlement value) {
    return new SettlementResponse(
        value.getSettlementId(),
        value.getClaimId(),
        value.getCoveredAmount(),
        value.getDeductibleApplied(),
        value.getDepreciation(),
        String.valueOf(value.isCappedAtLimit()),
        value.getSettlementAmount(),
        value.getCalculatedBy(),
        value.getCalculatedDate().toString());
  }
}

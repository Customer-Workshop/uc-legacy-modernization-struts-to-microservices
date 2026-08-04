package com.northstar.settlement.dto;

import com.northstar.settlement.service.CalculatedSettlement;
import java.util.List;

public record SettlementCalculationResponse(
    int claimId,
    double coveredAmount,
    double deductibleApplied,
    double depreciation,
    String cappedAtLimit,
    double settlementAmount,
    double policyLimit,
    List<String> validationErrors) {
  public static SettlementCalculationResponse from(CalculatedSettlement value) {
    return new SettlementCalculationResponse(
        value.claimId(),
        value.coveredAmount(),
        value.deductibleApplied(),
        value.depreciation(),
        Boolean.toString(value.cappedAtLimit()),
        value.settlementAmount(),
        value.policyLimit(),
        List.of());
  }
}

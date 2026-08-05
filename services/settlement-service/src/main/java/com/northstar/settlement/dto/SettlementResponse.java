package com.northstar.settlement.dto;

import com.northstar.settlement.service.SettlementCalculator;

public record SettlementResponse(
    String coveredAmount,
    String deductibleApplied,
    String depreciation,
    String cappedAtLimit,
    String settlementAmount) {
  public static SettlementResponse from(SettlementCalculator.Outcome outcome) {
    return new SettlementResponse(
        MoneyText.of(outcome.coveredAmount()),
        MoneyText.of(outcome.deductibleApplied()),
        MoneyText.of(outcome.depreciation()),
        String.valueOf(outcome.cappedAtLimit()),
        MoneyText.of(outcome.settlementAmount()));
  }
}

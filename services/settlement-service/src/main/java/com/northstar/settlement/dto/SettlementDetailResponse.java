package com.northstar.settlement.dto;

import com.northstar.settlement.model.Settlement;

public record SettlementDetailResponse(
    String settlementId,
    String claimId,
    String coveredAmount,
    String deductibleApplied,
    String depreciation,
    String cappedAtLimit,
    String amount,
    String calculatedBy,
    String calculatedDate) {
  public static SettlementDetailResponse from(Settlement s) {
    return new SettlementDetailResponse(
        s.getSettlementId().toString(),
        s.getClaimId().toString(),
        MoneyText.of(s.getCoveredAmount().doubleValue()),
        MoneyText.of(s.getDeductibleApplied().doubleValue()),
        MoneyText.of(s.getDepreciation().doubleValue()),
        String.valueOf(s.getCappedAtLimit()),
        MoneyText.of(s.getSettlementAmount().doubleValue()),
        s.getCalculatedBy(),
        s.getCalculatedDate().toString());
  }
}

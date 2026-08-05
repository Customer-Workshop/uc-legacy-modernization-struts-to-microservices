package com.northstar.settlement.dto;

import com.northstar.settlement.model.Settlement;
import java.math.BigDecimal;
import java.math.RoundingMode;

public record SettlementResponse(
    Integer settlementId,
    Integer claimId,
    String coveredAmount,
    String deductibleApplied,
    String depreciation,
    String cappedAtLimit,
    String settlementAmount,
    String savedBy,
    String calculatedDate) {
  public static SettlementResponse from(Settlement s) {
    return new SettlementResponse(
        s.getSettlementId(),
        s.getClaimId(),
        money(s.getCoveredAmount()),
        money(s.getDeductibleApplied()),
        money(s.getDepreciation()),
        String.valueOf(s.isCappedAtLimit()),
        money(s.getSettlementAmount()),
        s.getCalculatedBy(),
        s.getCalculatedDate().toString());
  }

  private static String money(BigDecimal value) {
    return value.setScale(2, RoundingMode.HALF_UP).toPlainString();
  }
}

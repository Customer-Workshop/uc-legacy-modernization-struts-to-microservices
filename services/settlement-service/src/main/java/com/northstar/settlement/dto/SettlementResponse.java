package com.northstar.settlement.dto;

import com.northstar.settlement.model.Settlement;
import com.northstar.settlement.service.LegacyMoney;

public record SettlementResponse(
    Integer settlementId,
    Integer claimId,
    String coveredAmount,
    String deductibleApplied,
    String depreciation,
    String cappedAtLimit,
    String amount,
    String calculatedBy,
    String calculatedDate) {
  public static SettlementResponse from(Settlement s) {
    return new SettlementResponse(
        s.getSettlementId(),
        s.getClaimId(),
        LegacyMoney.format(s.getCoveredAmount()),
        LegacyMoney.format(s.getDeductibleApplied()),
        LegacyMoney.format(s.getDepreciation()),
        String.valueOf(s.isCappedAtLimit()),
        LegacyMoney.format(s.getSettlementAmount()),
        s.getCalculatedBy(),
        s.getCalculatedDate().toString());
  }
}

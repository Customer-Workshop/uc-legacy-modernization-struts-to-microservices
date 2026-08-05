package com.northstar.settlement.dto;

import com.northstar.settlement.model.Settlement;

public record SettlementSaveResponse(
    String settlementId, String claimId, String savedBy, String settlementAmount) {
  public static SettlementSaveResponse from(Settlement settlement) {
    return new SettlementSaveResponse(
        settlement.getSettlementId().toString(),
        settlement.getClaimId().toString(),
        settlement.getCalculatedBy(),
        MoneyText.of(settlement.getSettlementAmount().doubleValue()));
  }
}

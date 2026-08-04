package com.northstar.settlement.dto;

import com.northstar.settlement.model.Settlement;
import java.util.List;

public record SettlementSaveResponse(
    int settlementId,
    int claimId,
    double settlementAmount,
    String savedBy,
    String cappedAtLimit,
    double coveredAmount,
    double deductibleApplied,
    double depreciation,
    List<String> validationErrors) {
  public static SettlementSaveResponse from(Settlement settlement) {
    return new SettlementSaveResponse(
        settlement.getSettlementId(),
        settlement.getClaimId(),
        settlement.getSettlementAmount(),
        settlement.getCalculatedBy(),
        Boolean.toString(settlement.isCappedAtLimit()),
        settlement.getCoveredAmount(),
        settlement.getDeductibleApplied(),
        settlement.getDepreciation(),
        List.of());
  }
}

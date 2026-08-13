package com.northstar.settlement.dto;

import com.northstar.settlement.model.Settlement;
import java.util.List;
import java.util.Locale;

public record SettlementResponse(
    String settlementAmount,
    String coveredAmount,
    String deductibleApplied,
    String depreciation,
    String cappedAtLimit,
    Integer claimId,
    Integer settlementId,
    String savedBy,
    List<String> validationErrors) {
  public static SettlementResponse calculated(Settlement s) {
    return new SettlementResponse(
        money(s.getSettlementAmount()),
        money(s.getCoveredAmount()),
        money(s.getDeductibleApplied()),
        money(s.getDepreciation()),
        Boolean.toString(s.isCappedAtLimit()),
        s.getClaimId(),
        null,
        null,
        List.of());
  }

  public static SettlementResponse saved(Settlement s) {
    return new SettlementResponse(
        money(s.getSettlementAmount()),
        money(s.getCoveredAmount()),
        money(s.getDeductibleApplied()),
        money(s.getDepreciation()),
        Boolean.toString(s.isCappedAtLimit()),
        s.getClaimId(),
        s.getSettlementId(),
        s.getCalculatedBy(),
        List.of());
  }

  public static SettlementResponse latest(Settlement s) {
    return new SettlementResponse(
        money(s.getSettlementAmount()),
        null,
        null,
        null,
        Boolean.toString(s.isCappedAtLimit()),
        s.getClaimId(),
        s.getSettlementId(),
        null,
        List.of());
  }

  private static String money(double value) {
    return String.format(Locale.US, "%.2f", value);
  }
}

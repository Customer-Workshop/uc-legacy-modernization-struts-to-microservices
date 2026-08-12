package com.northstar.settlement.dto;

public record SettlementCalcResponse(
    String claimId,
    String coveredAmount,
    String deductibleApplied,
    String depreciation,
    String cappedAtLimit,
    String settlementAmount) {}

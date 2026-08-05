package com.northstar.settlement.dto;

public record CalculationResponse(
    Integer claimId,
    String coveredAmount,
    String deductibleApplied,
    String depreciation,
    String cappedAtLimit,
    String settlementAmount,
    String policyLimit) {}

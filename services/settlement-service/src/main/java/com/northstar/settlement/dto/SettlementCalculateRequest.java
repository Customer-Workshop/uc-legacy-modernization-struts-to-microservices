package com.northstar.settlement.dto;

public record SettlementCalculateRequest(
    String claimId, String coveredAmount, String deductible, String depreciation) {}

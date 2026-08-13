package com.northstar.settlement.dto;

public record SettlementRequest(
    String claimId, String coveredAmount, String deductible, String depreciation) {}

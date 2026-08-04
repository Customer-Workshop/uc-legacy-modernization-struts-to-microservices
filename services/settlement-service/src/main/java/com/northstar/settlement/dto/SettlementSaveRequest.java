package com.northstar.settlement.dto;

public record SettlementSaveRequest(
    String claimId, String coveredAmount, String deductible, String depreciation, String savedBy) {}

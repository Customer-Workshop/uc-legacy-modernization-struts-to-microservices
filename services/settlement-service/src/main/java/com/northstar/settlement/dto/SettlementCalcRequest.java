package com.northstar.settlement.dto;

public record SettlementCalcRequest(
    String claimId, String coveredAmount, String deductible, String depreciation) {}

package com.northstar.settlement.dto;

public record SettlementSaveResponse(
    Integer settlementId, Integer claimId, double settlementAmount, String savedBy) {}

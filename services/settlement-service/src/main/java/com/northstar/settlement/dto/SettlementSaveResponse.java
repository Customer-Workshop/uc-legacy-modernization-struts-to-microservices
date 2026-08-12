package com.northstar.settlement.dto;

public record SettlementSaveResponse(
    String settlementId, String claimId, String settlementAmount, String savedBy) {}

package com.northstar.settlement.dto;

/**
 * Mirrors the String-typed legacy {@code SettlementForm}; conversion and fallbacks are applied
 * explicitly in the service layer exactly as the Struts request parsing did.
 */
public record SettlementRequest(
    String claimId, String coveredAmount, String deductible, String depreciation) {}

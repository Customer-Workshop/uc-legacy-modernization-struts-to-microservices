package com.northstar.settlement.dto;

/** String-typed like the Struts SettlementForm so legacy coercions stay explicit. */
public record SettlementRequest(
    String claimId, String coveredAmount, String deductible, String depreciation) {}

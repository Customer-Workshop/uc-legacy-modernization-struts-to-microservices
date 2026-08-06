package com.northstar.settlement.dto;

/** String-typed like the Struts form bean so legacy coercions stay explicit. */
public record CalculateRequest(
    String claimId, String coveredAmount, String deductible, String depreciation) {}

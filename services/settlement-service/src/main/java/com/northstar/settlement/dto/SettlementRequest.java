package com.northstar.settlement.dto;

/** String-typed fields mirror the Struts form bean; coercions happen in the service layer. */
public record SettlementRequest(
    String claimId, String coveredAmount, String deductible, String depreciation) {}

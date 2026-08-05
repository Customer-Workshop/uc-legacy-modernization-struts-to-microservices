package com.northstar.workbench.dto;

public record ReserveResponse(String claimId, String reserveAmount, ClaimResponse claim) {}

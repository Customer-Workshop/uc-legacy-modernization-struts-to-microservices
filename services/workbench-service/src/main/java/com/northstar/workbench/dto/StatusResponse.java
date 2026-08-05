package com.northstar.workbench.dto;

public record StatusResponse(String claimId, String claimStatus, ClaimResponse claim) {}

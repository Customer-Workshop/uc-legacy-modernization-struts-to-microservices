package com.northstar.intake.dto;

import com.northstar.intake.model.Claim;

public record ClaimResponse(
    String claimId,
    String claimNumber,
    Integer policyId,
    String claimantName,
    String lossDate,
    String reportedDate,
    String lossType,
    String description,
    String status,
    String reserveAmount,
    String assignedAdjuster,
    String createdBy,
    String createdDate) {
  public static ClaimResponse from(Claim c) {
    return new ClaimResponse(
        c.getClaimId().toString(),
        c.getClaimNumber(),
        c.getPolicyId(),
        c.getClaimantName(),
        c.getLossDate().toString(),
        c.getReportedDate().toString(),
        c.getLossType(),
        c.getDescription(),
        c.getStatus(),
        c.getReserveAmount().setScale(2, java.math.RoundingMode.HALF_UP).toPlainString(),
        c.getAssignedAdjuster(),
        c.getCreatedBy(),
        c.getCreatedDate().toString());
  }
}

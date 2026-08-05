package com.northstar.workbench.dto;

import com.northstar.workbench.model.Claim;

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
    if (c == null) {
      return null;
    }
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

package com.northstar.intake.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "claim")
public class Claim {
  @Id private Integer claimId;
  private String claimNumber;
  private Integer policyId;
  private String claimantName;
  private LocalDate lossDate;
  private LocalDate reportedDate;
  private String lossType;
  private String description;
  private String status;
  private BigDecimal reserveAmount;
  private String assignedAdjuster;
  private String createdBy;
  private LocalDate createdDate;

  protected Claim() {}

  public Claim(
      Integer claimId,
      String claimNumber,
      Integer policyId,
      String claimantName,
      LocalDate lossDate,
      LocalDate reportedDate,
      String lossType,
      String description,
      String status,
      BigDecimal reserveAmount,
      String assignedAdjuster,
      String createdBy,
      LocalDate createdDate) {
    this.claimId = claimId;
    this.claimNumber = claimNumber;
    this.policyId = policyId;
    this.claimantName = claimantName;
    this.lossDate = lossDate;
    this.reportedDate = reportedDate;
    this.lossType = lossType;
    this.description = description;
    this.status = status;
    this.reserveAmount = reserveAmount;
    this.assignedAdjuster = assignedAdjuster;
    this.createdBy = createdBy;
    this.createdDate = createdDate;
  }

  public Integer getClaimId() {
    return claimId;
  }

  public String getClaimNumber() {
    return claimNumber;
  }

  public Integer getPolicyId() {
    return policyId;
  }

  public String getClaimantName() {
    return claimantName;
  }

  public LocalDate getLossDate() {
    return lossDate;
  }

  public LocalDate getReportedDate() {
    return reportedDate;
  }

  public String getLossType() {
    return lossType;
  }

  public String getDescription() {
    return description;
  }

  public String getStatus() {
    return status;
  }

  public BigDecimal getReserveAmount() {
    return reserveAmount;
  }

  public String getAssignedAdjuster() {
    return assignedAdjuster;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public LocalDate getCreatedDate() {
    return createdDate;
  }
}

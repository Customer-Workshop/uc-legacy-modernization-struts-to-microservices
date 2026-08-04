package com.northstar.settlement.model;

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

  public Integer getClaimId() {
    return claimId;
  }

  public Integer getPolicyId() {
    return policyId;
  }
}

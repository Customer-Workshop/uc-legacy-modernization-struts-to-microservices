package com.northstar.settlement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "claim")
public class Claim {
  @Id private Integer claimId;
  private Integer policyId;
  private String claimantName;
  private String status;

  protected Claim() {}

  public Integer getClaimId() {
    return claimId;
  }

  public Integer getPolicyId() {
    return policyId;
  }

  public String getClaimantName() {
    return claimantName;
  }

  public String getStatus() {
    return status;
  }
}

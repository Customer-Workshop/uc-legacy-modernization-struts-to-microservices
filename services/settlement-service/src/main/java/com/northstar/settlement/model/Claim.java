package com.northstar.settlement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "claim")
public class Claim {
  @Id private Integer claimId;
  private Integer policyId;

  protected Claim() {}

  public Claim(Integer claimId, Integer policyId) {
    this.claimId = claimId;
    this.policyId = policyId;
  }

  public Integer getClaimId() {
    return claimId;
  }

  public Integer getPolicyId() {
    return policyId;
  }
}

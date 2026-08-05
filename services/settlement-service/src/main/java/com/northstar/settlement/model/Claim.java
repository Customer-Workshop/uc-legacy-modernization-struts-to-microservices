package com.northstar.settlement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "claim")
public class Claim {
  @Id
  @Column(name = "claim_id")
  private Integer claimId;

  @Column(name = "policy_id")
  private Integer policyId;

  protected Claim() {}

  public Integer getClaimId() {
    return claimId;
  }

  public Integer getPolicyId() {
    return policyId;
  }
}

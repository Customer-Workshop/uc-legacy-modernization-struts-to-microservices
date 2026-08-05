package com.northstar.settlement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Read-only projection of the claim rows the settlement module joins against. */
@Entity
@Table(name = "claim")
public class Claim {
  @Id
  @Column(name = "claim_id")
  private Integer claimId;

  @Column(name = "policy_id")
  private Integer policyId;

  public Claim() {}

  public Integer getClaimId() {
    return claimId;
  }

  public Integer getPolicyId() {
    return policyId;
  }
}

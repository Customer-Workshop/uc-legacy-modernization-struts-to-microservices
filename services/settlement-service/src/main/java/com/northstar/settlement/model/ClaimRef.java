package com.northstar.settlement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "claim")
public class ClaimRef {
  @Id private Integer claimId;
  private Integer policyId;

  protected ClaimRef() {}

  public Integer getClaimId() {
    return claimId;
  }

  public Integer getPolicyId() {
    return policyId;
  }
}

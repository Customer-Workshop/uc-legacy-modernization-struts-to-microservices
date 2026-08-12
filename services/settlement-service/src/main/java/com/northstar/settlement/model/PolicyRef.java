package com.northstar.settlement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "policy")
public class PolicyRef {
  @Id private Integer policyId;
  private double policyLimit;

  protected PolicyRef() {}

  public Integer getPolicyId() {
    return policyId;
  }

  public double getPolicyLimit() {
    return policyLimit;
  }
}

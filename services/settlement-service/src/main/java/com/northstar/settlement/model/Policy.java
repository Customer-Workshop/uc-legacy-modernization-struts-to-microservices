package com.northstar.settlement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "policy")
public class Policy {
  @Id private Integer policyId;
  private Double policyLimit;

  protected Policy() {}

  public Policy(Integer policyId, Double policyLimit) {
    this.policyId = policyId;
    this.policyLimit = policyLimit;
  }

  public Integer getPolicyId() {
    return policyId;
  }

  public Double getPolicyLimit() {
    return policyLimit;
  }
}

package com.northstar.settlement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Read-only projection of the policy rows the settlement module joins against. */
@Entity
@Table(name = "policy")
public class Policy {
  @Id
  @Column(name = "policy_id")
  private Integer policyId;

  @Column(name = "policy_limit")
  private double policyLimit;

  public Policy() {}

  public Integer getPolicyId() {
    return policyId;
  }

  public double getPolicyLimit() {
    return policyLimit;
  }
}

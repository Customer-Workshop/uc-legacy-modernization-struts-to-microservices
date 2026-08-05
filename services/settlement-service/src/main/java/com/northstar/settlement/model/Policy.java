package com.northstar.settlement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "policy")
public class Policy {
  @Id
  @Column(name = "policy_id")
  private Integer policyId;

  @Column(name = "policy_limit")
  private BigDecimal policyLimit;

  protected Policy() {}

  public Integer getPolicyId() {
    return policyId;
  }

  public BigDecimal getPolicyLimit() {
    return policyLimit;
  }
}

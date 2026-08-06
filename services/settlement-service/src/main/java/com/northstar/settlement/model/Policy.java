package com.northstar.settlement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "policy")
public class Policy {
  @Id private Integer policyId;
  private BigDecimal policyLimit;

  protected Policy() {}

  public Integer getPolicyId() {
    return policyId;
  }

  public BigDecimal getPolicyLimit() {
    return policyLimit;
  }
}

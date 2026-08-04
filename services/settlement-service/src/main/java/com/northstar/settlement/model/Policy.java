package com.northstar.settlement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "policy")
public class Policy {
  @Id private Integer policyId;
  private String policyNumber;
  private String lineOfBusiness;
  private String insuredName;
  private String insuredAddress;
  private LocalDate effectiveDate;
  private LocalDate expiryDate;
  private BigDecimal policyLimit;
  private BigDecimal deductible;
  private BigDecimal annualPremium;
  private String status;

  protected Policy() {}

  public Integer getPolicyId() {
    return policyId;
  }

  public BigDecimal getPolicyLimit() {
    return policyLimit;
  }
}

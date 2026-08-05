package com.northstar.policy.model;

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

  public String getPolicyNumber() {
    return policyNumber;
  }

  public String getLineOfBusiness() {
    return lineOfBusiness;
  }

  public String getInsuredName() {
    return insuredName;
  }

  public String getInsuredAddress() {
    return insuredAddress;
  }

  public LocalDate getEffectiveDate() {
    return effectiveDate;
  }

  public LocalDate getExpiryDate() {
    return expiryDate;
  }

  public BigDecimal getPolicyLimit() {
    return policyLimit;
  }

  public BigDecimal getDeductible() {
    return deductible;
  }

  public BigDecimal getAnnualPremium() {
    return annualPremium;
  }

  public String getStatus() {
    return status;
  }
}

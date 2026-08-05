package com.northstar.settlement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "settlement")
public class Settlement {
  @Id private Integer settlementId;
  private Integer claimId;
  private BigDecimal coveredAmount;
  private BigDecimal deductibleApplied;
  private BigDecimal depreciation;
  private Boolean cappedAtLimit;
  private BigDecimal settlementAmount;
  private String calculatedBy;
  private LocalDate calculatedDate;

  protected Settlement() {}

  public Settlement(
      Integer settlementId,
      Integer claimId,
      BigDecimal coveredAmount,
      BigDecimal deductibleApplied,
      BigDecimal depreciation,
      Boolean cappedAtLimit,
      BigDecimal settlementAmount,
      String calculatedBy,
      LocalDate calculatedDate) {
    this.settlementId = settlementId;
    this.claimId = claimId;
    this.coveredAmount = coveredAmount;
    this.deductibleApplied = deductibleApplied;
    this.depreciation = depreciation;
    this.cappedAtLimit = cappedAtLimit;
    this.settlementAmount = settlementAmount;
    this.calculatedBy = calculatedBy;
    this.calculatedDate = calculatedDate;
  }

  public Integer getSettlementId() {
    return settlementId;
  }

  public Integer getClaimId() {
    return claimId;
  }

  public BigDecimal getCoveredAmount() {
    return coveredAmount;
  }

  public BigDecimal getDeductibleApplied() {
    return deductibleApplied;
  }

  public BigDecimal getDepreciation() {
    return depreciation;
  }

  public Boolean getCappedAtLimit() {
    return cappedAtLimit;
  }

  public BigDecimal getSettlementAmount() {
    return settlementAmount;
  }

  public String getCalculatedBy() {
    return calculatedBy;
  }

  public LocalDate getCalculatedDate() {
    return calculatedDate;
  }
}

package com.northstar.settlement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "settlement")
public class Settlement {
  @Id
  @Column(name = "settlement_id")
  private Integer settlementId;

  @Column(name = "claim_id")
  private Integer claimId;

  @Column(name = "covered_amount")
  private BigDecimal coveredAmount;

  @Column(name = "deductible_applied")
  private BigDecimal deductibleApplied;

  @Column(name = "depreciation")
  private BigDecimal depreciation;

  @Column(name = "capped_at_limit")
  private boolean cappedAtLimit;

  @Column(name = "settlement_amount")
  private BigDecimal settlementAmount;

  @Column(name = "calculated_by")
  private String calculatedBy;

  @Column(name = "calculated_date")
  private LocalDate calculatedDate;

  protected Settlement() {}

  public Settlement(
      Integer settlementId,
      Integer claimId,
      BigDecimal coveredAmount,
      BigDecimal deductibleApplied,
      BigDecimal depreciation,
      boolean cappedAtLimit,
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

  public boolean isCappedAtLimit() {
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

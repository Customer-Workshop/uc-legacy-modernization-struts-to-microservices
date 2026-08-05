package com.northstar.settlement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "settlement")
public class Settlement {
  @Id
  @Column(name = "settlement_id")
  private Integer settlementId;

  @Column(name = "claim_id")
  private Integer claimId;

  @Column(name = "covered_amount")
  private double coveredAmount;

  @Column(name = "deductible_applied")
  private double deductibleApplied;

  @Column(name = "depreciation")
  private double depreciation;

  @Column(name = "capped_at_limit")
  private boolean cappedAtLimit;

  @Column(name = "settlement_amount")
  private double settlementAmount;

  @Column(name = "calculated_by")
  private String calculatedBy;

  @Column(name = "calculated_date")
  private String calculatedDate;

  public Settlement() {}

  public Integer getSettlementId() {
    return settlementId;
  }

  public void setSettlementId(Integer settlementId) {
    this.settlementId = settlementId;
  }

  public Integer getClaimId() {
    return claimId;
  }

  public void setClaimId(Integer claimId) {
    this.claimId = claimId;
  }

  public double getCoveredAmount() {
    return coveredAmount;
  }

  public void setCoveredAmount(double coveredAmount) {
    this.coveredAmount = coveredAmount;
  }

  public double getDeductibleApplied() {
    return deductibleApplied;
  }

  public void setDeductibleApplied(double deductibleApplied) {
    this.deductibleApplied = deductibleApplied;
  }

  public double getDepreciation() {
    return depreciation;
  }

  public void setDepreciation(double depreciation) {
    this.depreciation = depreciation;
  }

  public boolean isCappedAtLimit() {
    return cappedAtLimit;
  }

  public void setCappedAtLimit(boolean cappedAtLimit) {
    this.cappedAtLimit = cappedAtLimit;
  }

  public double getSettlementAmount() {
    return settlementAmount;
  }

  public void setSettlementAmount(double settlementAmount) {
    this.settlementAmount = settlementAmount;
  }

  public String getCalculatedBy() {
    return calculatedBy;
  }

  public void setCalculatedBy(String calculatedBy) {
    this.calculatedBy = calculatedBy;
  }

  public String getCalculatedDate() {
    return calculatedDate;
  }

  public void setCalculatedDate(String calculatedDate) {
    this.calculatedDate = calculatedDate;
  }
}

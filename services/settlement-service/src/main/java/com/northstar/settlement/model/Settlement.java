package com.northstar.settlement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "settlement")
public class Settlement {
  @Id private Integer settlementId;
  private Integer claimId;
  private double coveredAmount;
  private double deductibleApplied;
  private double depreciation;
  private boolean cappedAtLimit;
  private double settlementAmount;
  private String calculatedBy;
  private LocalDate calculatedDate;

  protected Settlement() {}

  public Settlement(
      Integer settlementId,
      Integer claimId,
      double coveredAmount,
      double deductibleApplied,
      double depreciation,
      boolean cappedAtLimit,
      double settlementAmount,
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

  public double getCoveredAmount() {
    return coveredAmount;
  }

  public double getDeductibleApplied() {
    return deductibleApplied;
  }

  public double getDepreciation() {
    return depreciation;
  }

  public boolean isCappedAtLimit() {
    return cappedAtLimit;
  }

  public double getSettlementAmount() {
    return settlementAmount;
  }

  public String getCalculatedBy() {
    return calculatedBy;
  }

  public LocalDate getCalculatedDate() {
    return calculatedDate;
  }
}

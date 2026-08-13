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
  private double settlementAmount;
  private boolean cappedAtLimit;
  private String calculatedBy;
  private LocalDate calculatedDate;

  protected Settlement() {}

  public Settlement(
      Integer id,
      Integer claimId,
      double covered,
      double deductible,
      double depreciation,
      double amount,
      boolean capped,
      String by,
      LocalDate date) {
    this.settlementId = id;
    this.claimId = claimId;
    this.coveredAmount = covered;
    this.deductibleApplied = deductible;
    this.depreciation = depreciation;
    this.settlementAmount = amount;
    this.cappedAtLimit = capped;
    this.calculatedBy = by;
    this.calculatedDate = date;
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

  public double getSettlementAmount() {
    return settlementAmount;
  }

  public boolean isCappedAtLimit() {
    return cappedAtLimit;
  }

  public String getCalculatedBy() {
    return calculatedBy;
  }

  public LocalDate getCalculatedDate() {
    return calculatedDate;
  }
}

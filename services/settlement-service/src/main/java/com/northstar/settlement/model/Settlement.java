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
  private Double coveredAmount;
  private Double deductibleApplied;
  private Double depreciation;
  private Boolean cappedAtLimit;
  private Double settlementAmount;
  private String calculatedBy;
  private LocalDate calculatedDate;

  protected Settlement() {}

  public Settlement(
      Integer settlementId,
      Integer claimId,
      Double coveredAmount,
      Double deductibleApplied,
      Double depreciation,
      Boolean cappedAtLimit,
      Double settlementAmount,
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

  public Double getCoveredAmount() {
    return coveredAmount;
  }

  public Double getDeductibleApplied() {
    return deductibleApplied;
  }

  public Double getDepreciation() {
    return depreciation;
  }

  public Boolean getCappedAtLimit() {
    return cappedAtLimit;
  }

  public Double getSettlementAmount() {
    return settlementAmount;
  }

  public String getCalculatedBy() {
    return calculatedBy;
  }

  public LocalDate getCalculatedDate() {
    return calculatedDate;
  }
}

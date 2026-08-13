package com.northstar.settlement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "payment")
public class Payment {
  @Id private Integer paymentId;
  private Integer claimId;
  private Integer settlementId;
  private String payeeName;
  private double amount;
  private String paymentMethod;
  private String checkNumber;
  private LocalDate issuedDate;
  private String status;

  protected Payment() {}

  public Payment(
      Integer id,
      Integer claim,
      Integer settlement,
      String payee,
      double amount,
      String method,
      String check,
      LocalDate date,
      String status) {
    this.paymentId = id;
    this.claimId = claim;
    this.settlementId = settlement;
    this.payeeName = payee;
    this.amount = amount;
    this.paymentMethod = method;
    this.checkNumber = check;
    this.issuedDate = date;
    this.status = status;
  }

  public Integer getPaymentId() {
    return paymentId;
  }

  public Integer getClaimId() {
    return claimId;
  }

  public Integer getSettlementId() {
    return settlementId;
  }

  public String getCheckNumber() {
    return checkNumber;
  }

  public double getAmount() {
    return amount;
  }

  public String getStatus() {
    return status;
  }

  public LocalDate getIssuedDate() {
    return issuedDate;
  }
}

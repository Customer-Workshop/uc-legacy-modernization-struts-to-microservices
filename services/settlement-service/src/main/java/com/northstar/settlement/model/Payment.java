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
  private Double amount;
  private String paymentMethod;
  private String checkNumber;
  private LocalDate issuedDate;
  private String status;

  protected Payment() {}

  public Payment(
      Integer paymentId,
      Integer claimId,
      Integer settlementId,
      String payeeName,
      Double amount,
      String paymentMethod,
      String checkNumber,
      LocalDate issuedDate,
      String status) {
    this.paymentId = paymentId;
    this.claimId = claimId;
    this.settlementId = settlementId;
    this.payeeName = payeeName;
    this.amount = amount;
    this.paymentMethod = paymentMethod;
    this.checkNumber = checkNumber;
    this.issuedDate = issuedDate;
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

  public String getPayeeName() {
    return payeeName;
  }

  public Double getAmount() {
    return amount;
  }

  public String getPaymentMethod() {
    return paymentMethod;
  }

  public String getCheckNumber() {
    return checkNumber;
  }

  public LocalDate getIssuedDate() {
    return issuedDate;
  }

  public String getStatus() {
    return status;
  }
}

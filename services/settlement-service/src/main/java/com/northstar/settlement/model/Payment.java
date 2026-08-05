package com.northstar.settlement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "payment")
public class Payment {
  @Id
  @Column(name = "payment_id")
  private Integer paymentId;

  @Column(name = "claim_id")
  private Integer claimId;

  @Column(name = "settlement_id")
  private Integer settlementId;

  @Column(name = "payee_name")
  private String payeeName;

  @Column(name = "amount")
  private double amount;

  @Column(name = "payment_method")
  private String paymentMethod;

  @Column(name = "check_number")
  private String checkNumber;

  @Column(name = "issued_date")
  private String issuedDate;

  @Column(name = "status")
  private String status;

  public Payment() {}

  public Integer getPaymentId() {
    return paymentId;
  }

  public void setPaymentId(Integer paymentId) {
    this.paymentId = paymentId;
  }

  public Integer getClaimId() {
    return claimId;
  }

  public void setClaimId(Integer claimId) {
    this.claimId = claimId;
  }

  public Integer getSettlementId() {
    return settlementId;
  }

  public void setSettlementId(Integer settlementId) {
    this.settlementId = settlementId;
  }

  public String getPayeeName() {
    return payeeName;
  }

  public void setPayeeName(String payeeName) {
    this.payeeName = payeeName;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public String getPaymentMethod() {
    return paymentMethod;
  }

  public void setPaymentMethod(String paymentMethod) {
    this.paymentMethod = paymentMethod;
  }

  public String getCheckNumber() {
    return checkNumber;
  }

  public void setCheckNumber(String checkNumber) {
    this.checkNumber = checkNumber;
  }

  public String getIssuedDate() {
    return issuedDate;
  }

  public void setIssuedDate(String issuedDate) {
    this.issuedDate = issuedDate;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}

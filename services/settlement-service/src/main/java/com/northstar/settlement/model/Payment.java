package com.northstar.settlement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

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
  private BigDecimal amount;

  @Column(name = "payment_method")
  private String paymentMethod;

  @Column(name = "check_number")
  private String checkNumber;

  @Column(name = "issued_date")
  private LocalDate issuedDate;

  @Column(name = "status")
  private String status;

  protected Payment() {}

  public Payment(
      Integer paymentId,
      Integer claimId,
      Integer settlementId,
      String payeeName,
      BigDecimal amount,
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

  public BigDecimal getAmount() {
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

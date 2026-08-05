package com.northstar.settlement.dto;

/** String-typed like the Struts PaymentForm so legacy coercions stay explicit. */
public record PaymentRequest(
    String claimId, String payeeName, String amount, String paymentMethod) {}

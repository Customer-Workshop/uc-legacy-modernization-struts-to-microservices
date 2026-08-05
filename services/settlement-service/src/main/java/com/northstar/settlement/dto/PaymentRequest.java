package com.northstar.settlement.dto;

/** String-typed fields mirror the Struts form bean; coercions happen in the service layer. */
public record PaymentRequest(
    String claimId, String payeeName, String amount, String paymentMethod) {}

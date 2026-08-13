package com.northstar.settlement.dto;

public record PaymentRequest(
    String claimId, String amount, String payeeName, String paymentMethod) {}

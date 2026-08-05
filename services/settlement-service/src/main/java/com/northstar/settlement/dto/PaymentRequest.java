package com.northstar.settlement.dto;

public record PaymentRequest(
    String claimId, String payeeName, String amount, String paymentMethod) {}

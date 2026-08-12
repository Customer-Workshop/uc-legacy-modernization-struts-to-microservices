package com.northstar.settlement.dto;

public record PaymentIssueRequest(
    String claimId, String payeeName, String amount, String paymentMethod) {}

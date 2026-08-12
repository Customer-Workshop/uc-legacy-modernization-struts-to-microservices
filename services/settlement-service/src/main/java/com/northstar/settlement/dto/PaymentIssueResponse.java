package com.northstar.settlement.dto;

public record PaymentIssueResponse(
    String paymentId,
    String claimId,
    String paymentAmount,
    String checkNumber,
    String paymentStatus) {}

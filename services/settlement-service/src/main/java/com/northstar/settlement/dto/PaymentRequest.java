package com.northstar.settlement.dto;

/** Mirrors the String-typed legacy {@code PaymentForm}. */
public record PaymentRequest(
    String claimId, String payeeName, String amount, String paymentMethod) {}

package com.northstar.settlement.dto;

import java.util.List;

public record RemittanceResponse(
    Integer claimId, Integer paymentCount, String paymentTotal, List<PaymentResponse> payments) {}

package com.northstar.settlement.dto;

import java.util.List;

public record PaymentHistoryResponse(
    Integer claimId, Integer paymentCount, List<PaymentResponse> payments) {}

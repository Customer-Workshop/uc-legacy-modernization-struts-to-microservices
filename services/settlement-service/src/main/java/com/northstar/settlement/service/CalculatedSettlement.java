package com.northstar.settlement.service;

public record CalculatedSettlement(
    int claimId,
    double coveredAmount,
    double deductibleApplied,
    double depreciation,
    boolean cappedAtLimit,
    double settlementAmount,
    double policyLimit) {}

package com.northstar.reporting.dto;

public record LossRatioRow(
    String lineOfBusiness, double premiumTotal, double lossTotal, double lossRatio) {}

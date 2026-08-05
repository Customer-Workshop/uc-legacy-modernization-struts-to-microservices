package com.northstar.reporting.dto;

public record LossRatioRow(
    String lineOfBusiness, String premiumTotal, String lossTotal, String lossRatio) {}

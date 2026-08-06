package com.northstar.settlement.service;

import org.springframework.stereotype.Component;

/** Applies depreciation, deductible, policy cap, and cent rounding. */
@Component
public class SettlementCalculator {

  public record Calculation(
      double coveredAmount,
      double deductibleApplied,
      double depreciation,
      boolean cappedAtLimit,
      double settlementAmount) {}

  public Calculation calculate(
      double coveredAmount, String deductible, double depreciation, double policyLimit) {
    double deductibleValue = 0;
    if (deductible != null && deductible.trim().length() > 0) {
      deductibleValue = Double.parseDouble(deductible);
    }
    double gross = coveredAmount - depreciation;
    double afterDeductible = gross - deductibleValue;
    if (afterDeductible < 0) {
      afterDeductible = 0;
    }
    boolean capped = afterDeductible > policyLimit;
    double amount = capped ? policyLimit : afterDeductible;
    // legacy-faithful: double arithmetic with Math.round pays 1.00 on a 1.005 half cent.
    double rounded = Math.round(amount * 100.0) / 100.0;
    return new Calculation(coveredAmount, deductibleValue, depreciation, capped, rounded);
  }
}

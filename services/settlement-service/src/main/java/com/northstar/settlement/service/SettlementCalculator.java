package com.northstar.settlement.service;

import org.springframework.stereotype.Component;

/**
 * Settlement arithmetic carried over value-for-value from the legacy {@code SettlementCalculator}.
 */
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
    // legacy-faithful: double arithmetic with Math.round, not BigDecimal half-up, so a
    // covered amount of 1.005 rounds down to 1.00 exactly as the legacy application pays.
    double rounded = Math.round(amount * 100.0) / 100.0;
    return new Calculation(coveredAmount, deductibleValue, depreciation, capped, rounded);
  }
}

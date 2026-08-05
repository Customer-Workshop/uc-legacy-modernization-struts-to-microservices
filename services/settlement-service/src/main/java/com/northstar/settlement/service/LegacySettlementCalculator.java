package com.northstar.settlement.service;

/**
 * Port of the legacy SettlementCalculator. Arithmetic stays in {@code double} on purpose: the
 * transcripts pin the legacy rounding outcomes.
 */
public final class LegacySettlementCalculator {

  public record Calculation(
      double coveredAmount,
      double deductibleApplied,
      double depreciation,
      boolean cappedAtLimit,
      double settlementAmount) {}

  private LegacySettlementCalculator() {}

  public static Calculation calculate(
      double coveredAmount, String deductible, double depreciation, double policyLimit) {
    double deductibleValue = 0;
    if (deductible != null && deductible.trim().length() > 0) {
      deductibleValue = Double.parseDouble(deductible);
    }
    double gross = coveredAmount - depreciation;
    double afterDeductible = gross - deductibleValue;
    if (afterDeductible < 0) {
      // legacy-faithful: a deductible larger than the loss floors the settlement at zero.
      afterDeductible = 0;
    }
    boolean capped = afterDeductible > policyLimit;
    double amount = capped ? policyLimit : afterDeductible;
    // legacy-faithful: double + Math.round money math; 1.005 pays 1.00, not BigDecimal's 1.01.
    double rounded = Math.round(amount * 100.0) / 100.0;
    return new Calculation(coveredAmount, deductibleValue, depreciation, capped, rounded);
  }
}

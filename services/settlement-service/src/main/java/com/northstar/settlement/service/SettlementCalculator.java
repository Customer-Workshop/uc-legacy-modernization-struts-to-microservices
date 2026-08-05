package com.northstar.settlement.service;

/**
 * Settlement arithmetic carried over from the legacy {@code SettlementCalculator}. All math is
 * intentionally performed in {@code double} with {@code Math.round} cent rounding because the
 * legacy application computed money that way; a covered amount of 1.005 pays 1.00, not the 1.01
 * that exact decimal arithmetic would produce.
 */
public final class SettlementCalculator {

  private SettlementCalculator() {}

  public record Outcome(
      double coveredAmount,
      double deductibleApplied,
      double depreciation,
      boolean cappedAtLimit,
      double settlementAmount) {}

  public static Outcome calculate(
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
    // legacy-faithful: double half-cent rounding via Math.round, not BigDecimal HALF_UP.
    double rounded = Math.round(amount * 100.0) / 100.0;
    return new Outcome(coveredAmount, deductibleValue, depreciation, capped, rounded);
  }
}

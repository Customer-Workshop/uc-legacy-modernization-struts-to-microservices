package com.northstar.settlement.service;

/**
 * Value-for-value port of the legacy {@code SettlementCalculator}. Arithmetic is deliberately kept
 * in {@code double} with {@code Math.round} cent rounding: the legacy application has always paid
 * 1.00 on a 1.005 covered amount because the binary double sits just below the half cent. Switching
 * to BigDecimal would silently change issued amounts (see settlement_half_cent).
 */
public final class SettlementCalculator {
  private SettlementCalculator() {}

  public record Result(
      double coveredAmount,
      double deductibleApplied,
      double depreciation,
      boolean cappedAtLimit,
      double settlementAmount) {}

  public static Result calculate(
      double coveredAmount, String deductible, double depreciation, double policyLimit) {
    // legacy-faithful: blank deductible coerces to zero (SettlementCalculator.java:26-29).
    double deductibleValue = 0;
    if (deductible != null && deductible.trim().length() > 0) {
      deductibleValue = Double.parseDouble(deductible);
    }
    double gross = coveredAmount - depreciation;
    double afterDeductible = gross - deductibleValue;
    // legacy-faithful: deductible above the loss floors the settlement at zero.
    if (afterDeductible < 0) {
      afterDeductible = 0;
    }
    boolean capped = afterDeductible > policyLimit;
    double amount = capped ? policyLimit : afterDeductible;
    // legacy-faithful: double half-cent rounding (SettlementCalculator.java:37).
    double rounded = Math.round(amount * 100.0) / 100.0;
    return new Result(coveredAmount, deductibleValue, depreciation, capped, rounded);
  }
}

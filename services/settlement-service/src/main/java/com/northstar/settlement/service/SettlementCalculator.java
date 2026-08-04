package com.northstar.settlement.service;

import org.springframework.stereotype.Component;

@Component
public class SettlementCalculator {
  public CalculatedSettlement calculate(
      int claimId,
      double coveredAmount,
      String deductible,
      double depreciation,
      double policyLimit) {
    double deductibleValue = 0;
    // legacy-faithful: blank deductible strings become numeric zero.
    if (deductible != null && deductible.trim().length() > 0) {
      deductibleValue = Double.parseDouble(deductible);
    }
    double gross = coveredAmount - depreciation;
    double afterDeductible = gross - deductibleValue;
    // legacy-faithful: the deductible floor runs before policy-limit capping.
    if (afterDeductible < 0) {
      afterDeductible = 0;
    }
    // legacy-faithful: capping is strict greater-than.
    boolean capped = afterDeductible > policyLimit;
    double amount = capped ? policyLimit : afterDeductible;
    // legacy-faithful: Java double half-cent rounding happens last.
    double rounded = Math.round(amount * 100.0) / 100.0;
    return new CalculatedSettlement(
        claimId, coveredAmount, deductibleValue, depreciation, capped, rounded, policyLimit);
  }
}
